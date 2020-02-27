package ru.vinokurov;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {

    @FXML private JFXTreeView<TreeViewData> treeView = new JFXTreeView();
    @FXML private JFXTextField searchField = new JFXTextField();
    @FXML private JFXTextField fileExtensionField = new JFXTextField();
    @FXML private JFXTabPane tabPane = new JFXTabPane();
    @FXML private AnchorPane contentPain = new AnchorPane();
    private Image nodeImgFolder = new Image(getClass().getResourceAsStream("folder.png"));
    private Image nodeImgFile = new Image(getClass().getResourceAsStream("icons8-file-25.png"));

    /**
     * ClickListener for search button
     */
    @FXML
    public void findInDirectory() {

//        searchField.setText("VinInd");
//        fileExtensionField.setText(".txt");
        String fileExtensionValue = fileExtensionField.getText();
        String searchFieldValue   = searchField.getText();

        if (fileExtensionValue.trim().isEmpty()
                || searchFieldValue.trim().isEmpty()) {
            showAlert("Ошибка ввода", "Введите текст поиска и расшиирение файла.");
            return;
        }

        // clear tree
        if (treeView.getRoot() != null) {
            treeView.getRoot().getChildren().clear();
            treeView.setRoot(null);
        }

        // chose directory
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File choice = directoryChooser.showDialog(App.stage);

        if(choice == null || ! choice.isDirectory() || !choice.canRead()) {
            showAlert("Could not open directory", "The file is invalid.");
        } else {
            treeView.setRoot(getNodesForDirectory(choice, " ", fileExtensionValue));
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
            System.out.println("Loading " + file.getName());
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

    @FXML
    private JFXTextArea textArea = new JFXTextArea();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        treeView.setOnMouseClicked((mouseEvent -> {

            if(mouseEvent.getClickCount() == 2) {
                textArea.setText("");
                TreeItem<TreeViewData> item = treeView.getSelectionModel().getSelectedItem();
                File file = item.getValue().getFile();
                try {
                    Files.lines(file.toPath()).forEach((value) -> {
                        textArea.appendText(value);
                        textArea.appendText("\n");
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }));
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
