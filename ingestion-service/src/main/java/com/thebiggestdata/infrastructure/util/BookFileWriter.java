package com.thebiggestdata.infrastructure.util;

import java.io.FileWriter;
import java.io.IOException;

public class BookFileWriter {
    public void write(String path, String content) {
        try (FileWriter writer = new FileWriter(path)) {writer.write(content);}
        catch (IOException e) {throw new RuntimeException("Failed to write file: " + path, e);}
    }
}
