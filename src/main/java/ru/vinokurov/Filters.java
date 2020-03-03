package ru.vinokurov;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This class contains various methods for manipulating files (such as checking extension and finding text).
 */
public class Filters {

    public static boolean equalsFileExtension(File file, String extension) {
        return file.getName().endsWith(extension);
    }

    public static boolean containsTextInFile(File file, String text) {
        Optional<String> optional = Optional.empty();

        try (Stream<String> stream = Files.lines(file.toPath())){
            optional = stream.filter((line) -> line.contains(text)).findAny();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return optional.isPresent();
    }

}
