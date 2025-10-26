package com.thebiggestdata.searchservice.repository;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.thebiggestdata.searchservice.model.BookInfo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Repository
public class MetadataRepository {
    private final Logger logger = LoggerFactory.getLogger(MetadataRepository.class);
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public void initialize(String uri, String databaseName, String collectionName) {
        try {
            this.mongoClient = MongoClients.create(uri);
            this.database = mongoClient.getDatabase(databaseName);
            this.collection = database.getCollection(collectionName);
            logger.info("Initialized metadata repository: {}.{}", databaseName, collectionName);
        } catch (MongoException e) {
            logger.error("Failed to initialize metadata repository: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to metadata database", e);
        }
    }

    public List<BookInfo> findBooksByIds(Set<Integer> bookIds) {
        if (collection == null) {
            logger.error("Repository not initialized");
            return List.of();
        }

        try {
            List<BookInfo> books = new ArrayList<>();
            Document query = new Document("book_id", new Document("$in", new ArrayList<>(bookIds)));

            collection.find(query).forEach(doc -> {
                BookInfo book = documentToBookInfo(doc);
                if (book != null) {
                    books.add(book);
                }
            });

            logger.debug("Retrieved {} books from metadata", books.size());
            return books;

        } catch (MongoException e) {
            logger.error("Error retrieving books by IDs: {}", e.getMessage());
            return List.of();
        }
    }

    private BookInfo documentToBookInfo(Document doc) {
        try {
            int bookId = doc.getInteger("book_id");
            String title = doc.getString("title");
            String author = doc.getString("author");
            String language = doc.getString("language");
            Integer year = extractYearFromReleaseDate(doc.getString("release_date"));

            return new BookInfo(bookId, title, author, language, year);

        } catch (Exception e) {
            logger.warn("Error parsing document to BookInfo: {}", e.getMessage());
            return null;
        }
    }

    private Integer extractYearFromReleaseDate(String releaseDate) {
        if (releaseDate == null || releaseDate.isBlank()) {
            return null;
        }

        try {
            Pattern yearPattern = Pattern.compile("\\b(\\d{4})\\b");
            java.util.regex.Matcher matcher = yearPattern.matcher(releaseDate);

            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            logger.debug("Could not extract year from release_date: {}", releaseDate);
        }

        return null;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            logger.info("Closed metadata repository connection");
        }
    }
}