package com.thebiggestdata.domain.service;

import com.thebiggestdata.domain.model.Book;
import com.thebiggestdata.domain.model.StorageResult;
import com.thebiggestdata.domain.port.in.FetchBooksUseCase;
import com.thebiggestdata.domain.port.in.StoreBookUseCase;
import com.thebiggestdata.domain.port.out.BookFetcherPort;
import com.thebiggestdata.domain.port.out.BookStoragePort;
import com.thebiggestdata.domain.port.out.FileSystemPort;
import com.thebiggestdata.infrastructure.adapter.out.serializer.BookSerializer;
import java.time.LocalDateTime;
import java.util.logging.Logger;


public class BookIngestionService implements FetchBooksUseCase, StoreBookUseCase {
    private static final Logger logger = Logger.getLogger(BookIngestionService.class.getName());
    private final BookFetcherPort bookFetcherPort;
    private final FileSystemPort fileSystemPort;
    private final BookStoragePort bookStoragePort;

    public BookIngestionService(
            BookFetcherPort bookFetcherPort,
            FileSystemPort fileSystemPort,
            BookStoragePort bookStoragePort) {
        this.bookFetcherPort = bookFetcherPort;
        this.fileSystemPort = fileSystemPort;
        this.bookStoragePort = bookStoragePort;
    }

    @Override
    public Book fetchBook(int bookId) throws Exception {
        logger.info("Fetching book with ID: " + bookId);
        String content = bookFetcherPort.fetchBookContent(bookId);
        BookSerializer serializer = new BookSerializer(content, bookId);
        Book book = serializer.serialize();
        logger.info("Successfully fetched and serialized book: " + bookId);
        return book;
    }

    @Override
    public StorageResult storeBook(Book book) {return storeBook(book, LocalDateTime.now());}

    @Override
    public StorageResult storeBook(Book book, LocalDateTime timestamp) {
        logger.info("Storing book with ID: " + book.bookId());
        StorageResult result = fileSystemPort.save(book, timestamp);
        if (result.success()) logger.info("Successfully stored book: " + book.bookId());
        else logger.warning("Failed to store book: " + book.bookId());
        return result;
    }

    public boolean bookExists(int bookId) {return bookStoragePort.exists(bookId);}
}
