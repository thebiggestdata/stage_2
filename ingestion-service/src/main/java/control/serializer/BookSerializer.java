package control.serializer;

import model.Book;

public class BookSerializer {
    private final String START_MARKER = "*** START OF THE PROJECT GUTENBERG EBOOK";
    private final String END_MARKER = "*** END OF THE PROJECT GUTENBERG EBOOK";

    public BookSerializer() {

    }

    public Book serialize(String bookContent) {
        if (!isValidBook(bookContent)) throw new IllegalArgumentException("Invalid book content");
        return new Book(
                extractBookId(bookContent),
                extractHeader(bookContent),
                extractBody(bookContent),
                extractFooter(bookContent)
        );
    }

    private String extractBody(String bookContent) {
        return null;
    }

    private String extractHeader(String bookContent) {
        return null;
    }

    private String extractFooter(String bookContent) {
        return null;
    }

    private int extractBookId(String bookContent) {
        return 0;
    }

    private boolean isValidBook(String bookContent) {
        return bookContent.contains(START_MARKER) && bookContent.contains(END_MARKER);
    }
}
