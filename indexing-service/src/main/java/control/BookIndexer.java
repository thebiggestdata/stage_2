package control;

import model.Processor;
import control.mongoDb.DbInterface;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class BookIndexer {
    private final Logger logger = Logger.getLogger(BookIndexer.class.getName());
    private final DbInterface invertedIndex;
    private final Processor textProcessor;
    private final Path datalakeBasePath;

    public BookIndexer(DbInterface invertedIndex, Processor textProcessor, String datalakeBasePath) {
        this.invertedIndex = invertedIndex;
        this.textProcessor = textProcessor != null ? textProcessor : new Processor();
        this.datalakeBasePath = Paths.get(datalakeBasePath);
        logger.info("BookIndexer initialized");
    }

    public BookIndexer(DbInterface invertedIndex, String datalakeBasePath) {this(invertedIndex, null, datalakeBasePath);}

    public BookIndexer(DbInterface invertedIndex) {
        this(invertedIndex, null, "datalake/");
    }

    public boolean indexBook(int bookId, String downloadDate, String downloadHour) {
        try {
            String bookText = readBookBody(bookId, downloadDate, downloadHour);
            if (bookText == null) {
                logger.severe(String.format("Failed to read book %d, skipping indexing", bookId));
                return false;
            }
            List<String> processedTerms = textProcessor.process(bookText);
            logger.info(String.format("Book %d: extracted %d tokens (after stopword filtering)", bookId, processedTerms.size()));
            Set<String> uniqueTerms = new HashSet<>(processedTerms);
            int successfulUpdates = 0;
            for (String term : uniqueTerms) {
                if (invertedIndex.addDocumentToTerm(term, String.valueOf(bookId))) successfulUpdates++;
                else logger.warning(String.format("Failed to add term '%s' for book %d", term, bookId));
            }
            logger.info(String.format("Book %d: successfully indexed %d unique terms out of %d", bookId, successfulUpdates, uniqueTerms.size()));
            return successfulUpdates > 0;
        } catch (Exception e) {
            logger.severe(String.format("Unexpected error indexing book %d: %s", bookId, e.getMessage()));
            return false;
        }
    }

    private String readBookBody(int bookId, String downloadDate, String downloadHour) {
        try {
            Path bodyPath = datalakeBasePath.resolve(downloadDate).resolve(downloadHour).resolve(bookId + ".body.txt");
            if (!Files.exists(bodyPath)) {
                logger.severe(String.format("Body file not found: %s", bodyPath));
                return null;
            }
            String content = Files.readString(bodyPath, StandardCharsets.UTF_8);
            if (content.trim().isEmpty()) {
                logger.warning(String.format("Body file is empty for book %d", bookId));
                return null;
            }
            logger.fine(String.format("Successfully read %d characters from book %d", content.length(), bookId));
            return content;
        } catch (IOException e) {
            logger.severe(String.format("Failed to read body file for book %d: %s", bookId, e.getMessage()));
            return null;
        } catch (Exception e) {
            logger.severe(String.format("Encoding error reading book %d.\n %s", bookId, e.getMessage()));
            return null;
        }
    }

    public Map<String, Object> getIndexStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("total_unique_terms", invertedIndex.getIndexSize());
        statistics.put("index_type", invertedIndex.getClass().getSimpleName());
        return statistics;
    }
}
