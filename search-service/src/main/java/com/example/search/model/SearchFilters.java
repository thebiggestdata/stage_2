package com.thebiggestdata.searchservice.model;

public class SearchFilters {
    private String author;
    private String language;
    private Integer year;

    public SearchFilters() {}

    public SearchFilters(String author, String language, Integer year) {
        this.author = author;
        this.language = language;
        this.year = year;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

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