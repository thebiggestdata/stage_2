package com.example.search.model;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String language;
    private int year;

    public Book(int bookId, String title, String author, String language, int year) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.language = language;
        this.year = year;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getLanguage() { return language; }
    public int getYear() { return year; }

    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setLanguage(String language) { this.language = language; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", language='" + language + '\'' +
                ", year=" + year +
                '}';
    }
}