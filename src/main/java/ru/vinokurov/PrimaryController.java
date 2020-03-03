package ru.vinokurov;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {

    @FXML private JFXTreeView<TreeViewData> treeView = new JFXTreeView();
    @FXML private JFXTextField searchField = new JFXTextField();
    @FXML private JFXTextField fileExtensionField = new JFXTextField();
    @FXML private JFXTabPane tabPane = new JFXTabPane();
    @FXML private AnchorPane contentPain = new AnchorPane();
    @FXML private JFXProgressBar progressBar = new JFXProgressBar();
    @FXML private JFXButton searchButton = new JFXButton();
    private Image nodeImgFolder = new Image(getClass().getResourceAsStream("folder.png"));
    private Image nodeImgFile = new Image(getClass().getResourceAsStream("icons8-file-25.png"));

    /**
     * ClickListener for search button
     */
    @FXML
    public void findInDirectory() {
        String fileExtensionValue = fileExtensionField.getText();
        String searchFieldValue   = searchField.getText();

        if (fileExtensionValue.trim().isEmpty()
                || searchFieldValue.trim().isEmpty()) {
            showAlert("Ошибка ввода",
                    "Введите текст поиска и расширение файла.",
                    Alert.AlertType.ERROR
            );
            return;
        }

        // choose directory
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File choice = directoryChooser.showDialog(App.stage);

        if(choice == null || ! choice.isDirectory() || !choice.canRead()) {
            showAlert("Невозможно открыть файл.",
                    "Возможно, у вас недостаточно прав для чтения этого файла.",
                    Alert.AlertType.ERROR
            );
        } else {
            //clear treeView
            if (treeView.getRoot() != null) {
                treeView.getRoot().getChildren().clear();
                treeView.setRoot(null);
            }

            searchButton.setDisable(true);

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    progressBar.setVisible(true);
                    TreeItem<TreeViewData> treeRoot = getNodesForDirectory(choice, searchFieldValue, fileExtensionValue);
                    Platform.runLater(() -> treeView.setRoot(treeRoot));
                    progressBar.setVisible(false);
                    searchButton.setDisable(false);
                    return null;
                }
            };

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private boolean firstVision = true;

    /**
     * ClickListener for tab opener button
     */
    @FXML
    private void openInNewTab() {
        TreeItem<TreeViewData> item = treeView.getSelectionModel()
                .getSelectedItem();

        if (item == null ) {
            showAlert(
                    "Файл не выбран.",
                    "Выберите файл и повторите попытку",
                    Alert.AlertType.ERROR
            );
            return;
        }

        File file = item.getValue().getFile();

        if (file.isDirectory()) {
            showAlert(
                    "Выбрана директория.",
                    "Выберите файл и повторите попытку",
                    Alert.AlertType.ERROR
            );
            return;
        }

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                progressBar.setVisible(true);
                Tab tab = new Tab(file.getName());
                BigFileView b = new BigFileView(file.toPath());
                tab.setContent(new AnchorPane(b.getContent()));
                tab.setClosable(true);
                Platform.runLater(() -> tabPane.getTabs().add(tab));
                progressBar.setVisible(false);
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

         if (firstVision) {
             showAlert(
                     "Подксказка",
                     "Вы можете закрыть вкладку дваждый кликнув на нее",
                     Alert.AlertType.INFORMATION
             );
             firstVision = false;
         }
    }

    /**
     * Recursive method of building files directory tree
     * @param directory root directory node for children-tree
     * @param text search text
     * @param extension extension of file
     * @return root TreeItem
     */
    private TreeItem<TreeViewData> getNodesForDirectory(File directory, String text, String extension) {
        TreeItem<TreeViewData> root = new TreeItem<>(new TreeViewData(directory), new ImageView(nodeImgFolder));

        for (File file: directory.listFiles()) {
            if (file.isDirectory()) {
                root.getChildren().add(getNodesForDirectory(file, text, extension));
            } else {
                if (Filters.equalsFileExtension(file, extension) && Filters.containsTextInFile(file, text)) {
                    root.getChildren().add(new TreeItem<>(
                            new TreeViewData(file),
                            new ImageView(nodeImgFile))
                    );
                }
            }
        }
        root.setExpanded(true);
        return root;
    }

    private void showAlert(String headerText, String contentText, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Double ClickListener on treeView item
        treeView.setOnMouseClicked((mouseEvent -> {

            if(mouseEvent.getClickCount() == 2) {
                treeView.setDisable(true);
                contentPain.getChildren().clear();
                Path itemPath = treeView.getSelectionModel()
                        .getSelectedItem()
                        .getValue()
                        .getFile()
                        .toPath();

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        progressBar.setVisible(true);
                        BigFileView bigFileView = new BigFileView(itemPath);
                        Platform.runLater(() -> {
                            contentPain.getChildren().add(bigFileView.getContent());
                            treeView.setDisable(false);
                            progressBar.setVisible(false);
                        });
                        return null;
                    }
                };
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            }
        }));

        // Double tap for closing of current tab
        tabPane.setOnMouseClicked((mouseEvent -> {

            if(mouseEvent.getClickCount() == 2) {
                Tab tab = tabPane.getSelectionModel().getSelectedItem();
                if (tab.getText().equals("Главный экран")) {
                    showAlert(
                            "Ошибка выбора.",
                            "Нельзя закрыть главную вкладку.",
                            Alert.AlertType.ERROR
                    );
                    return;
                }
                tabPane.getTabs().remove(tab);
            }
        }));
    }


    /**
     * The wrapper class for the File.
     * Used to override toString method and support custom name view in treeView.
     */
    public static class TreeViewData  {

        private File file;

        public TreeViewData(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        @Override
        public String toString() {
            return file.getName();
        }
    }
}
