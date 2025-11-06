package com.thebiggestdata.domain.port.out;

import com.thebiggestdata.domain.model.Book;
import com.thebiggestdata.domain.model.StorageResult;
import java.time.LocalDateTime;


public interface FileSystemPort {
    StorageResult save(Book book, LocalDateTime timestamp);
}
