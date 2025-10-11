package control.storer;

import java.io.File;

public class FileSystemManager {
    public void ensureDirectoryExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public String buildPath(String directory, String fileName) {
        return directory + File.separator + fileName;
    }
}