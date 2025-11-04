package com.thebiggestdata.ingestion.control.storer;

import java.io.IOException;
import java.io.FileWriter;

public class BookFileWriter {
    public void write(String path, String content) {
        try (FileWriter writer = new FileWriter(path)) {writer.write(content);}
        catch (IOException e) {throw new RuntimeException("Failed to write file: " + path, e);}
    }
}
