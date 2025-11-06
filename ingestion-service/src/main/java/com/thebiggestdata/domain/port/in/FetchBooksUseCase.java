package com.thebiggestdata.domain.port.in;
import com.thebiggestdata.domain.model.Book;

public interface FetchBooksUseCase {
    Book fetchBook(int bookId) throws Exception;
}
