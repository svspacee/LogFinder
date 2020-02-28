package ru.vinokurov;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
    //@FXML private Pagination pagination = new Pagination();
    private Image nodeImgFolder = new Image(getClass().getResourceAsStream("folder.png"));
    private Image nodeImgFile = new Image(getClass().getResourceAsStream("icons8-file-25.png"));

    /**
     * ClickListener for search button
     */
    @FXML
    public void findInDirectory() {
        fileExtensionField.setText(".txt");
        searchField.setText(" ");
        String fileExtensionValue = fileExtensionField.getText();
        String searchFieldValue   = searchField.getText();

//        if (fileExtensionValue.trim().isEmpty()
//                || searchFieldValue.trim().isEmpty()) {
//            showAlert("Ошибка ввода", "Введите текст поиска и расшиирение файла.");
//            return;
//        }

        // clear tree
        if (treeView.getRoot() != null) {
            treeView.getRoot().getChildren().clear();
            treeView.setRoot(null);
        }

        // chose directory
//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
//        File choice = directoryChooser.showDialog(App.stage);

        File choice = new File("C:\\Users\\vinok\\OneDrive\\Рабочий стол\\TestFolder");

        if(choice == null || ! choice.isDirectory() || !choice.canRead()) {
            showAlert("Could not open directory", "The file is invalid.");
        } else {
            treeView.setRoot(getNodesForDirectory(choice, " ", fileExtensionValue));
        }
    }

    @FXML
    private void openInNewTab() {
        TreeItem<TreeViewData> item = treeView.getSelectionModel()
                .getSelectedItem();


        if (item == null) {
            showAlert(
                    "Файл не выбран.",
                    "Выберите файл и повторите попытку"
            );

            return;
        }

        File file = item.getValue()
                .getFile();
        Tab tab = new Tab(file.getName());
        tab.setContent(new BigFileView(contentPain, file.toPath()).getContent());
        tab.setClosable(true);
        tabPane.getTabs().add(tab);


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

                if (Filters.equalsFileExtension(file, extension)
                        && Filters.findTextInFile(file, text)) {
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


    private void showAlert(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Double ClickListener on treeView item
        treeView.setOnMouseClicked((mouseEvent -> {

            if(mouseEvent.getClickCount() == 2) {
                Path itemPath = treeView.getSelectionModel()
                        .getSelectedItem()
                        .getValue()
                        .getFile()
                        .toPath();
                new BigFileView(contentPain, itemPath);
            }
        }));
    }

    private Node createPage(int pageIndex) {

        Label lab = new Label();
        lab.setText(Integer.toString(pageIndex));

        return new BorderPane(lab);
    }

    /**
     * The wrapper class for the File.
     * Used to override toString method and support custom name view in treeView.
     */
    public static class TreeViewData {

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
