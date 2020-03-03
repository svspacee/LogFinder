package ru.vinokurov;


import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class TestDirFinder {

    private static final String FILE_PATH = "";
    private final String keyWord = "final";
    private long start = 0;

    @Test
    public void findWithBufferedReader() {
        start = System.currentTimeMillis();

        boolean fileHavingText = false;
        try {
            int count = 0;
            FileReader fileIn = new FileReader(new File(FILE_PATH));
            BufferedReader reader = new BufferedReader(fileIn);
            String line;
            while((line = reader.readLine()) != null) {
                if((line.lastIndexOf(keyWord) > -1)) {
                    fileHavingText = true;
                    break;
                }
            }
        }catch (IOException e){
            System.out.println(e);
        }
        System.out.println("BufIO Time: " + (System.currentTimeMillis() - start) + " seconds");
        Assert.assertTrue(fileHavingText);
    }


    /**
     * Test of a search text with basic Files stream.
     */
    @Test
    public void findTextWithStream() {
        start = System.currentTimeMillis();

        Path path = Paths.get(FILE_PATH);

        Optional<String> optional = null;
        try (Stream<String> stream = Files.lines(path)){
            optional = stream.filter((line) -> line.contains("final")).findAny();
            optional.ifPresent(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No file");
        }

        System.out.println("Files line Time: " + (System.currentTimeMillis() - start) + " seconds");

        Assert.assertTrue( optional.isPresent());
    }
}
