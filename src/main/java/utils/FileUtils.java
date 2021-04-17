package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class FileUtils {
    public static void createFile(String path, String name) {
        try {
            File file = new File(path + name);
            file.createNewFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void createFolder(String path) {
        try {
            File file = new File(path);
            file.mkdir();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean folderExists(String path) {
        try {
            File file = new File(path);
            return file.exists() && file.isDirectory();
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean fileExists(String path) {
        try {
            File file = new File(path);
            return file.exists() && file.isFile();
        } catch (Exception ex) {
            return false;
        }
    }

    public static void writeLineToFile(String filePath, boolean writeToNewLine, String... args) {
        if(writeToNewLine) goToNewLine(filePath);
        String line = String.join(",", args);
        appendToFile(filePath, line);
    }

    private static void goToNewLine(String filepath) {
        appendToFile(filepath, "\n");
    }

    private static void appendToFile(String filepath, String data) {
        try {
            FileWriter fileWriter = new FileWriter(filepath, true);
            fileWriter.append(data);
            fileWriter.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String readNthLine(String filePath, int n) {
        String line = null;
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            if(n > 0) line = lines.skip(n-1).findFirst().get();
            else line = lines.findFirst().get();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return line;
    }

    public static void writeToFile(String filePath, Object o) {
        try {
            File fileOne = new File(filePath);
            FileOutputStream fos = new FileOutputStream(fileOne);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(o);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static <T> T readFromFile(String filePath) {
        try {
            File toRead = new File(filePath);
            FileInputStream fis = new FileInputStream(toRead);
            ObjectInputStream ois = new ObjectInputStream(fis);

            T object = (T) ois.readObject();

            ois.close();
            fis.close();

            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
