package com.thebiggestdata.domain.exception;

public class BookFetchException extends Exception {
    private final int bookId;

    public BookFetchException(int bookId, String message) {
        super(String.format("Failed to fetch book %d: %s", bookId, message));
        this.bookId = bookId;
    }

    public BookFetchException(int bookId, String message, Throwable cause) {
        super(String.format("Failed to fetch book %d: %s", bookId, message), cause);
        this.bookId = bookId;
    }

    public int getBookId() {
        return bookId;
    }
}

