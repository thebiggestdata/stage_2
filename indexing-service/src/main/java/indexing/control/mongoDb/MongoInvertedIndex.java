package indexing.control.mongoDb;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MongoInvertedIndex implements DbInterface {
    private final Logger logger = Logger.getLogger(MongoInvertedIndex.class.getName());
    private final String connectionString;
    private final String databaseName;
    private final String collectionName;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoInvertedIndex(String connectionString, String databaseName, String collectionName) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }

    public MongoInvertedIndex() {this("mongodb://localhost:27017/", "inverted_index", "words");}

    @Override
    public boolean connect(String uri, String dbName) {
        try {
            mongoClient = MongoClients.create(uri != null ? uri : connectionString);
            database = mongoClient.getDatabase(dbName != null ? dbName : databaseName);
            collection = database.getCollection(collectionName);
            collection.createIndex(Indexes.ascending("term"), new IndexOptions().unique(true));
            logger.info(String.format("Initialized MongoDB inverted index: %s.%s", databaseName, collectionName));
            return true;
        } catch (MongoException e) {
            logger.severe("Failed to initialize MongoDB inverted index: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addDocumentToTerm(String term, String bookId) {
        if (collection == null) {
            logger.severe("Index not initialized. Call connect() first.");
            return false;
        }
        try {
            collection.updateOne(
                    new Document("term", term),
                    Updates.addToSet("postings", Integer.parseInt(bookId)),
                    new com.mongodb.client.model.UpdateOptions().upsert(true)
            );
            return true;
        } catch (MongoException | NumberFormatException e) {
            logger.severe(String.format("Failed to add document %s to term '%s': %s", bookId, term, e.getMessage()));
            return false;
        }
    }

    @Override
    public Set<Integer> seekDocumentsByTerm(String term) {
        if (collection == null) {
            logger.severe("Index not initialized. Call connect() first.");
            return Set.of();
        }
        try {
            Document document = collection.find(new Document("term", term)).first();
            if (document != null && document.containsKey("postings")) {
                List<Integer> postings = (List<Integer>) document.get("postings");
                return new HashSet<>(postings);
            }
            return Set.of();
        } catch (MongoException e) {
            logger.severe(String.format("Failed to get documents for term '%s': %s", term, e.getMessage()));
            return Set.of();
        }
    }

    @Override
    public List<String> getAllTerms() {
        if (collection == null) {
            logger.severe("Index not initialized. Call connect() first.");
            return List.of();
        }
        try {
            return collection.find()
                    .projection(new Document("term", 1).append("_id", 0))
                    .sort(new Document("term", 1))
                    .into(new ArrayList<>())
                    .stream()
                    .map(doc -> doc.getString("term"))
                    .collect(Collectors.toList());
        } catch (MongoException e) {
            logger.severe("Failed to get all terms: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public int getIndexSize() {
        if (collection == null) {
            logger.severe("Index not initialized. Call connect() first.");
            return 0;
        }
        try {return (int) collection.countDocuments();}
        catch (MongoException e) {
            logger.severe("Failed to get index size: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            logger.info("Closed MongoDB inverted index connection");
        }
    }
}