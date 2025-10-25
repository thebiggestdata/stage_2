package com.example.search.repository;

import com.example.search.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class InvertedIndexRepository {
    private static final Logger logger = LoggerFactory.getLogger(InvertedIndexRepository.class);

    private final String datamartPath;
    private Map<String, Set<Integer>> invertedIndex;
    private Map<Integer, Book> metadata;

    public InvertedIndexRepository(String datamartPath) {
        this.datamartPath = datamartPath;
        this.invertedIndex = new HashMap<>();
        this.metadata = new HashMap<>();
        loadIndex();
    }

    public void loadIndex() {
        logger.info("Loading inverted index from: {}", datamartPath);

        try {
            loadInvertedIndex();
            loadMetadata();
            logger.info("Index loaded successfully. Terms: {}, Books: {}",
                    invertedIndex.size(), metadata.size());
        } catch (Exception e) {
            logger.error("Error loading index", e);
            throw new RuntimeException("Failed to load index", e);
        }
    }

    private void loadInvertedIndex() throws IOException {
        Path indexPath = Paths.get(datamartPath, "inverted_index.txt");

        if (!Files.exists(indexPath)) {
            logger.warn("Inverted index file not found: {}", indexPath);
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(indexPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2) {
                    String word = parts[0].toLowerCase().trim();
                    String[] bookIds = parts[1].split(",");

                    Set<Integer> ids = Arrays.stream(bookIds)
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .collect(Collectors.toSet());

                    invertedIndex.put(word, ids);
                }
            }
        }
    }

    private void loadMetadata() throws IOException {
        Path metadataPath = Paths.get(datamartPath, "metadata.txt");

        if (!Files.exists(metadataPath)) {
            logger.warn("Metadata file not found: {}", metadataPath);
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(metadataPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 5) {
                    int bookId = Integer.parseInt(parts[0].trim());
                    String title = parts[1].trim();
                    String author = parts[2].trim();
                    String language = parts[3].trim();
                    int year = Integer.parseInt(parts[4].trim());

                    Book book = new Book(bookId, title, author, language, year);
                    metadata.put(bookId, book);
                }
            }
        }
    }

    public Set<Integer> searchTerm(String term) {
        String normalizedTerm = normalizeTerm(term);
        return invertedIndex.getOrDefault(normalizedTerm, new HashSet<>());
    }

    public Book getBook(int bookId) {
        return metadata.get(bookId);
    }

    public List<Book> getBooks(Set<Integer> bookIds) {
        return bookIds.stream()
                .map(metadata::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(metadata.values());
    }

    private String normalizeTerm(String term) {
        if (term == null) return "";
        return term.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]", "");
    }

    public void refresh() {
        logger.info("Refreshing index...");
        invertedIndex.clear();
        metadata.clear();
        loadIndex();
    }

    public int getIndexSize() {
        return invertedIndex.size();
    }

    public int getBookCount() {
        return metadata.size();
    }
}