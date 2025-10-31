package metadata.control.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.UpdateOptions;
import metadata.model.BookMetadata;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MongoDbStorage implements DbStorageInterface {
    private final Logger logger = LoggerFactory.getLogger(MongoDbStorage.class);
    private final String connectionString;
    private final String databaseName;
    private final String collectionName;
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDbStorage(String connectionString, String databaseName, String collectionName) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }

    public MongoDbStorage() {
        this("mongodb://localhost:27017/", "metadata", "books");
    }

    @Override
    public boolean initialize() {
        try {
            this.client = MongoClients.create(connectionString);
            this.database = client.getDatabase(databaseName);
            this.collection = database.getCollection(collectionName);
            collection.createIndex(
                    Indexes.ascending("book_id"),
                    new IndexOptions().unique(true)
            );
            collection.createIndex(Indexes.ascending("author"));
            collection.createIndex(Indexes.ascending("language"));
            collection.createIndex(Indexes.text("title"));
            logger.info("Initialized MongoDB metadata storage: {}.{}", databaseName, collectionName);
            return true;
        } catch (MongoException e) {
            logger.error("Failed to initialize MongoDB metadata storage: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean insertBookMetadata(BookMetadata metadata) {
        if (collection == null) {
            logger.error("Storage not initialized. Call initialize() first.");
            return false;
        }
        try {
            Document document = new Document()
                    .append("book_id", metadata.bookId())
                    .append("title", metadata.title())
                    .append("author", metadata.author())
                    .append("language", metadata.language())
                    .append("release_date", metadata.releaseDate());
            collection.updateOne(
                    new Document("book_id", metadata.bookId()),
                    new Document("$set", document),
                    new UpdateOptions().upsert(true)
            );
            logger.debug("Inserted/Updated metadata for book {}", metadata.bookId());
            return true;
        } catch (MongoException e) {
            logger.error("Failed to insert metadata for book {}: {}", metadata.bookId(), e.getMessage());
            return false;
        }
    }

    @Override
    public BookMetadata getBookById(int bookId) {
        if (collection == null) {
            logger.error("Storage not initialized. Call initialize() first.");
            return null;
        }
        try {
            Document document = collection.find(new Document("book_id", bookId)).first();
            if (document != null) return documentToMetadata(document);
            return null;
        } catch (MongoException e) {
            logger.error("Failed to get book {}: {}", bookId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<BookMetadata> getBooksByAuthor(String author) {
        if (collection == null) {
            logger.error("Storage not initialized. Call initialize() first.");
            return new ArrayList<>();
        }
        try {
            Pattern pattern = Pattern.compile(author, Pattern.CASE_INSENSITIVE);
            List<BookMetadata> books = new ArrayList<>();
            collection.find(new Document("author", pattern))
                    .sort(new Document("title", 1))
                    .forEach(doc -> books.add(documentToMetadata(doc)));
            return books;
        } catch (MongoException e) {
            logger.error("Failed to get books by author '{}': {}", author, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<BookMetadata> getAllBooks(int limit) {
        if (collection == null) {
            logger.error("Storage not initialized. Call initialize() first.");
            return new ArrayList<>();
        }
        try {
            List<BookMetadata> books = new ArrayList<>();
            collection.find()
                    .sort(new Document("book_id", 1))
                    .limit(limit > 0 ? limit : 0)
                    .forEach(doc -> books.add(documentToMetadata(doc)));
            return books;
        } catch (MongoException e) {
            logger.error("Failed to get all books: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public int getTotalBooks() {
        if (collection == null) {
            logger.error("Storage not initialized. Call initialize() first.");
            return 0;
        }
        try {
            return (int) collection.countDocuments();
        } catch (MongoException e) {
            logger.error("Failed to get total books count: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean bookExists(int bookId) {
        if (collection == null) {
            logger.error("Storage not initialized. Call initialize() first.");
            return false;
        }
        try {
            return collection.countDocuments(new Document("book_id", bookId)) > 0;
        } catch (MongoException e) {
            logger.error("Failed to check if book {} exists: {}", bookId, e.getMessage());
            return false;
        }
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
            logger.info("Closed MongoDB metadata storage connection");
        }
    }

    private BookMetadata documentToMetadata(Document document) {
        return new BookMetadata(
                document.getInteger("book_id"),
                document.getString("title"),
                document.getString("author"),
                document.getString("language"),
                document.getString("release_date")
        );
    }
}
