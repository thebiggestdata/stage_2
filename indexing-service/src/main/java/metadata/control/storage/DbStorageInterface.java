package metadata.control.storage;

import metadata.model.BookMetadata;
import java.util.List;

public interface DbStorageInterface {
    boolean initialize();
    boolean insertBookMetadata(BookMetadata metadata);
    BookMetadata getBookById(int bookId);
    List<BookMetadata> getBooksByAuthor(String author);
    List<BookMetadata> getAllBooks(int limit);
    int getTotalBooks();
    boolean bookExists(int bookId);
    void close();
}
