package com.thebiggestdata.domain.exception;

public class InvalidBookContentException extends RuntimeException {
    private final int bookId;

    public InvalidBookContentException(int bookId, String message) {
        super(String.format("Invalid content for book %d: %s", bookId, message));
        this.bookId = bookId;
    }

    public int getBookId() {
        return bookId;
    }
}

