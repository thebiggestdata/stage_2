package control.storer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemManager {

    public void ensureDirectoryExists(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public String buildPath(String directory, String fileName) {
        return Paths.get(directory, fileName).toString();
    }
}
