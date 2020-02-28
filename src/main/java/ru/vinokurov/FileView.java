package ru.vinokurov;

import javafx.scene.control.Pagination;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileView {
    private final long countOfAreaLines = 100;
    private long countOfFileLines;
    private long countOfPages;

    public FileView(Path path, Pagination pagination) {

        try {
            countOfFileLines = getCountOfFileLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        countOfPages = countOfFileLines / 100;
    }

    private long getCountOfFileLines(Path path) throws IOException {
        return Files.lines(path).count();
    }

    private void setDataOnPage() {
        System.out.println("hello world");
    }
}
