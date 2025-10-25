package com.example.search.model;

import java.util.Objects;

public class SearchQuery {
    private String query;
    private String author;
    private String language;
    private Integer year;

    public SearchQuery(String query, String author, String language, Integer year) {
        this.query = query;
        this.author = author;
        this.language = language;
        this.year = year;
    }

    public String getQuery() { return query; }
    public String getAuthor() { return author; }
    public String getLanguage() { return language; }
    public Integer getYear() { return year; }

    public void setQuery(String query) { this.query = query; }
    public void setAuthor(String author) { this.author = author; }
    public void setLanguage(String language) { this.language = language; }
    public void setYear(Integer year) { this.year = year; }

    public boolean hasFilters() {
        return author != null || language != null || year != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchQuery that = (SearchQuery) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(author, that.author) &&
                Objects.equals(language, that.language) &&
                Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, author, language, year);
    }
}