package com.thebiggestdata.domain.exception;

public class BookStorageException extends RuntimeException {
    private final int bookId;

    public BookStorageException(int bookId, String message) {
        super(String.format("Failed to store book %d: %s", bookId, message));
        this.bookId = bookId;
    }

    public BookStorageException(int bookId, String message, Throwable cause) {
        super(String.format("Failed to store book %d: %s", bookId, message), cause);
        this.bookId = bookId;
    }

    public int getBookId() {
        return bookId;
    }
}

