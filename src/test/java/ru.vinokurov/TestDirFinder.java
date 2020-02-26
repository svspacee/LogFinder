package ru.vinokurov;


import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

public class TestDirFinder {

    private static final String FILE_PATH = "C:\\Users\\vinok\\OneDrive\\Рабочий стол\\test3gb.txt";
    private final String keyWord = "final";

    @Test
    public void test() {
        System.out.println(System.getProperty("java.class.path"));
    }
    @Test
    public void findWithBufferedReader() {

        boolean fileHavingText = false;
        try {
            int count = 0;
            FileReader fileIn = new FileReader(new File(FILE_PATH));
            BufferedReader reader = new BufferedReader(fileIn);
            String line;
            while((line = reader.readLine()) != null) {
                if((line.contains(keyWord))) {
                    fileHavingText = true;
                    break;
                }
            }
        }catch (IOException e){
            System.out.println(e);
        }

        assert fileHavingText;
    }

    /**
     * Test of a search text with basic Files stream.
     */
    @Test
    public void findTextWithStream() {
        Path path = Paths.get(FILE_PATH);

        Optional<String> optional = null;
        try (Stream<String> stream = Files.lines(path)){
            optional = stream.filter((line) -> line.contains(keyWord)).findAny();
            optional.ifPresent(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No file");
        }
        Assert.assertTrue( optional.isPresent());
    }


    @Test
    public void streamFile() {

        Path path = Paths.get(FILE_PATH);

        try (Stream<String> stream = Files.newBufferedReader(path).lines()){
            stream.forEach(value -> {});

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No file");
        }
    }

    @Test
    public void findTextWithScanner() {

        try (Scanner sc = new Scanner(new FileInputStream(FILE_PATH), "UTF-8") ){

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains(keyWord)) {
                    return;
                }
            }

            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
