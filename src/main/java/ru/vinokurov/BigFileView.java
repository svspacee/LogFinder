package ru.vinokurov;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * This class is intended for displaying selected file.
 * This class also contains two ways of displaying file depending on file size.
 */
public class BigFileView {
    private final long countOfAreaLines = 60;
    private long countOfPages = 0;
    private Path path;
    private Node node;

    public BigFileView (Path path) {
        this.path = path;
        long countOfFileLines = getCountOfFileLines(path);

        if (countOfFileLines < 5000) {
            node = setScrollGraphic();
        } else {
            countOfPages = countOfFileLines / countOfAreaLines;
            node = setPaginationGraphic();
        }
    }

    /**
     * Method for displaying small files.
     * @return new text area
     */
    public Node setScrollGraphic() {
        JFXTextArea area = new JFXTextArea();
        area.setEditable(false);
        area.setStyle("-fx-background-color: white;");
        AnchorPane.setTopAnchor(area, 5d);
        AnchorPane.setBottomAnchor(area, 5d);
        AnchorPane.setLeftAnchor(area,5d);
        AnchorPane.setRightAnchor(area, 5d);
        StringBuilder str = new StringBuilder();

        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(value -> str.append(value).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        area.setText(str.toString());
        return area;
    }

    /**
     * Method for displaying files on pagination view.
     * @return new Anchor pane with setting Pagination for
     * current file
     */
    private Node setPaginationGraphic() {

        Pagination pagination = new Pagination((int) countOfPages);
        pagination.setMaxPageIndicatorCount(100);
        pagination.setPageFactory(this::createPage);
        JFXButton firstPage = new JFXButton("К первой странице");
        firstPage.setOnMouseClicked((value) -> pagination.setCurrentPageIndex(0));
        JFXButton lastPage = new JFXButton("К последней странице");
        lastPage.setOnMouseClicked((value) -> pagination.setCurrentPageIndex((int) countOfPages));
        JFXTextField searchField = new JFXTextField();
        searchField.setPromptText("Введите номер страницы");
        JFXButton searchButton = new JFXButton("Перейти");
        searchButton.setOnMouseClicked(
                (value) -> pagination.setCurrentPageIndex(Integer.parseInt(searchField.getText()) - 1));
        HBox hBox = new HBox(firstPage, lastPage, searchField, searchButton);
        hBox.setSpacing(25);
        VBox vBox = new VBox(pagination, hBox);
        vBox.setFillWidth(true);
        VBox.setVgrow(pagination, Priority.ALWAYS );
        AnchorPane.setTopAnchor(vBox, 2d);
        AnchorPane.setBottomAnchor(vBox, 2d);
        AnchorPane.setLeftAnchor(vBox,2d);
        AnchorPane.setRightAnchor(vBox, 2d);

        return vBox;
    }

    private Node createPage(int pageIndex) {
        TextArea textArea = new JFXTextArea();
        textArea.setStyle("-fx-background-color: white;");
        textArea.setEditable(false);
        AnchorPane.setTopAnchor(textArea, 5d);
        AnchorPane.setBottomAnchor(textArea, 5d);
        AnchorPane.setLeftAnchor(textArea,5d);
        AnchorPane.setRightAnchor(textArea, 5d);
        StringBuilder str = new StringBuilder();

        try (Stream<String> stream = Files.lines(path)) {
            stream.skip(countOfAreaLines * pageIndex)
                    .limit(countOfAreaLines)
                    .forEach(value -> str.append(value).append("\n"));
            textArea.setText(str.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new AnchorPane(textArea);
    }


    public Node getContent() {
        return node;
    }

    private long getCountOfFileLines(Path path) {
        long count = 0;

        try (Stream<String> stream = Files.lines(path)) {
            count = stream.count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
}
