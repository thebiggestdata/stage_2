package com.thebiggestdata.search.model;

public record BookInfo(
        int bookId,
        String title,
        String author,
        String language,
        Integer year
) {}