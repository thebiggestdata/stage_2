package control.storer;

import model.Book;

public class BookFileNameGenerator {
    private static final String HEADER_SUFFIX = ".header.txt";
    private static final String BODY_SUFFIX = ".body.txt";

    public String generateHeaderFileName(Book book) {
        return book.bookId() + HEADER_SUFFIX;
    }

    public String generateBodyFileName(Book book) {
        return book.bookId() + BODY_SUFFIX;
    }
}
