package com.thebiggestdata.controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ProcessingTracker {
    private final String trackingFilePath;
    private final Set<Integer> processedBooks;

    public ProcessingTracker(String trackingFilePath) {
        this.trackingFilePath = trackingFilePath;
        this.processedBooks = new HashSet<>();
        loadProcessedBooks();
    }

    public ProcessingTracker() {
        this("processed_books.txt");
    }

    private void loadProcessedBooks() {
        Path path = Paths.get(trackingFilePath);

        if (!Files.exists(path)) {
            System.out.println("[TRACKER] No previous tracking file found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        int bookId = Integer.parseInt(line);
                        processedBooks.add(bookId);
                    } catch (NumberFormatException e) {
                        System.err.println("[TRACKER] Invalid book ID in tracking file: " + line);
                    }
                }
            }
            System.out.println("[TRACKER] Loaded " + processedBooks.size() + " previously processed books");
        } catch (IOException e) {
            System.err.println("[TRACKER] Error loading tracking file: " + e.getMessage());
        }
    }

    public boolean isProcessed(int bookId) {
        return processedBooks.contains(bookId);
    }

    public void markAsProcessed(int bookId) {
        if (processedBooks.add(bookId)) {
            saveToFile(bookId);
        }
    }

    private void saveToFile(int bookId) {
        try (FileWriter writer = new FileWriter(trackingFilePath, true)) {
            writer.write(bookId + "\n");
        } catch (IOException e) {
            System.err.println("[TRACKER] Error saving book " + bookId + " to tracking file: " + e.getMessage());
        }
    }

    public int getProcessedCount() {
        return processedBooks.size();
    }

    public void clear() {
        processedBooks.clear();
    }
}