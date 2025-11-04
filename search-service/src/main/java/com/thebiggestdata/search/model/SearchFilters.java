package com.thebiggestdata.search.model;

public record SearchFilters(String author, String language, Integer year) {

    public boolean hasAuthor() {
        return author != null && !author.isBlank();
    }

    public boolean hasLanguage() {
        return language != null && !language.isBlank();
    }

    public boolean hasYear() {
        return year != null;
    }

    public boolean hasAnyFilter() {
        return hasAuthor() || hasLanguage() || hasYear();
    }
}