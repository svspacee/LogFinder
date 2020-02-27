package ru.vinokurov;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Filter;
import java.util.stream.Stream;

public class Filters {

    private ExecutorService executorService;

    public Filters() {
        executorService = Executors.newSingleThreadExecutor();
    }
    public static boolean equalsFileExtension(File file, String extension) {
        return file.getName().endsWith(extension);
    }

    public static boolean findTextInFile(File file, String text) {


        Path path = Paths.get(file.getAbsolutePath());

        Optional<String> optional = null;
        try (Stream<String> stream = Files.lines(path)){
            optional = stream.filter((line) -> line.contains(text)).findAny();
            optional.ifPresent(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No file");
        }
        return optional.isPresent();
    }

}
