import control.fetch.BookFetcher;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        BookFetcher bookFetcher = new BookFetcher();
        try {
            System.out.println(bookFetcher.fetch(1234));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
