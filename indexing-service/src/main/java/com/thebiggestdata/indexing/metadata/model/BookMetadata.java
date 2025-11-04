package com.thebiggestdata.indexing.metadata.model;

public record BookMetadata (
    int bookId,
    String title,
    String author,
    String language,
    String releaseDate
)
{
    public boolean isComplete() {return bookId != 0 && title != null && !title.isEmpty();}

    @Override
    public String toString() {
        return String.format("BookMetadata{bookId=%d, title='%s', author='%s', language='%s'}",
                bookId, title, author, language, releaseDate);
    }
}
