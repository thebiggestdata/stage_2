package com.thebiggestdata.domain.port.in;

import com.thebiggestdata.domain.model.Book;
import com.thebiggestdata.domain.model.StorageResult;
import java.time.LocalDateTime;


public interface StoreBookUseCase {
    StorageResult storeBook(Book book);
    StorageResult storeBook(Book book, LocalDateTime timestamp);
}
