package api.mapper;

import api.dto.BookDto;
import api.dto.StorageResultDto;
import model.Book;
import model.StorageResult;

public class BookMapper {

    public static BookDto toDto(Book book) {
        if (book == null) return null;
        return new BookDto(book.bookId(), book.header(), book.body(), book.footer());
    }

    public static Book toModel(BookDto dto) {
        if (dto == null) return null;
        return new Book(dto.bookId(), dto.header(), dto.body(), dto.footer());
    }

    public static StorageResultDto toDto(StorageResult result) {
        if (result == null) return null;
        return new StorageResultDto(result.success(), result.headerPath(), result.bodyPath(), result.timestamp());
    }
}

