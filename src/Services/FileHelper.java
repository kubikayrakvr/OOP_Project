package Services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

// For the program to work as a jar
public class FileHelper {
    public static void ensureFileExists(String fileName) {
        File externalFile = new File(fileName);
        if (!externalFile.exists()) {
            try (InputStream is = FileHelper.class.getResourceAsStream("/" + fileName)) {
                if (is != null) {
                    Files.copy(is, externalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    externalFile.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}