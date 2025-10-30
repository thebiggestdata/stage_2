package com.thebiggestdata.searchservice.repository;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class InvertedIndexRepository {
    private final Logger logger = LoggerFactory.getLogger(InvertedIndexRepository.class);
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public void initialize(String uri, String databaseName, String collectionName) {
        try {
            this.mongoClient = MongoClients.create(uri);
            this.database = mongoClient.getDatabase(databaseName);
            this.collection = database.getCollection(collectionName);
            logger.info("Initialized inverted index repository: {}.{}", databaseName, collectionName);
        } catch (MongoException e) {
            logger.error("Failed to initialize inverted index repository: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to inverted index database", e);
        }
    }

    public Set<Integer> findBookIdsByTerm(String term) {
        if (collection == null) {
            logger.error("Repository not initialized");
            return Set.of();
        }

        try {
            var normalizedTerm = term.toLowerCase();
            var document = collection.find(new Document("term", normalizedTerm)).first();

            if (document != null && document.containsKey("postings")) {
                @SuppressWarnings("unchecked")
                List<Integer> postings = (List<Integer>) document.get("postings");
                logger.debug("Found {} books for term '{}'", postings.size(), term);
                return new HashSet<>(postings);
            }

            logger.debug("No books found for term '{}'", term);
            return Set.of();

        } catch (MongoException e) {
            logger.error("Error searching for term '{}': {}", term, e.getMessage());
            return Set.of();
        }
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            logger.info("Closed inverted index repository connection");
        }
    }
}