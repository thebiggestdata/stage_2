package indexing.control.mongoDb;

import java.util.List;
import java.util.Set;

public interface DbInterface {
    boolean connect(String uri, String dbName);
    boolean addDocumentToTerm(String docId, String content);
    Set<Integer> seekDocumentsByTerm(String term);
    List<String> getAllTerms();
    int getIndexSize();
    void close();
}
