package control.serializer;

import model.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BookSerializer {
    private final String START_MARKER = "*** START OF THE PROJECT GUTENBERG EBOOK";
    private final String END_MARKER = "*** END OF THE PROJECT GUTENBERG EBOOK";
    private final String bookContent;
    private final int bookId;

    public BookSerializer(String bookContent, int bookId) {
        this.bookContent = bookContent;
        this.bookId = bookId;
    }

    public Book serialize() {
        if (!isValidBook(bookContent)) throw new IllegalArgumentException("Invalid book markers");
        return new Book(
                bookId,
                extractHeader(),
                extractBody(),
                extractFooter()
        );
    }

    private String extractBody() {return splitContent(bookContent).get(1);}

    private String extractHeader() {return splitContent(bookContent).getFirst();}

    private String extractFooter() {return splitContent(bookContent).getLast();}

    private List<String> splitContent(String bookContent) {
        List<String> result = new ArrayList<>();
        String[] headerSplit = bookContent.split(Pattern.quote(START_MARKER), 2);
        String header = headerSplit[0];
        String[] bodyFooterSplit = headerSplit[1].split(Pattern.quote(END_MARKER), 2);
        String body = START_MARKER + bodyFooterSplit[0];
        String footer = END_MARKER + bodyFooterSplit[1];
        result.add(header);
        result.add(body);
        result.add(footer);
        return result;
    }

    private boolean isValidBook(String bookContent) {return bookContent.contains(START_MARKER) && bookContent.contains(END_MARKER);}
}