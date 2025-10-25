package com.example.search.service;

import com.example.search.model.Book;
import com.example.search.model.SearchQuery;
import com.example.search.repository.InvertedIndexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final InvertedIndexRepository repository;

    public SearchService(InvertedIndexRepository repository) {
        this.repository = repository;
    }

    public List<Book> search(SearchQuery query) {
        logger.info("Searching for: {}", query.getQuery());

        Set<Integer> bookIds = repository.searchTerm(query.getQuery());

        if (bookIds.isEmpty()) {
            logger.info("No results found for term: {}", query.getQuery());
            return Collections.emptyList();
        }

        List<Book> books = repository.getBooks(bookIds);
        List<Book> filteredBooks = applyFilters(books, query);

        filteredBooks.sort(Comparator.comparingInt(Book::getBookId));

        logger.info("Found {} results", filteredBooks.size());
        return filteredBooks;
    }

    private List<Book> applyFilters(List<Book> books, SearchQuery query) {
        return books.stream()
                .filter(book -> matchesAuthor(book, query.getAuthor()))
                .filter(book -> matchesLanguage(book, query.getLanguage()))
                .filter(book -> matchesYear(book, query.getYear()))
                .collect(Collectors.toList());
    }

    private boolean matchesAuthor(Book book, String author) {
        if (author == null || author.isBlank()) {
            return true;
        }
        return book.getAuthor() != null &&
                book.getAuthor().toLowerCase().contains(author.toLowerCase());
    }

    private boolean matchesLanguage(Book book, String language) {
        if (language == null || language.isBlank()) {
            return true;
        }
        return book.getLanguage() != null &&
                book.getLanguage().equalsIgnoreCase(language);
    }

    private boolean matchesYear(Book book, Integer year) {
        if (year == null) {
            return true;
        }
        return book.getYear() == year;
    }

    public void refreshIndex() {
        logger.info("Refreshing search index");
        repository.refresh();
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("indexed_terms", repository.getIndexSize());
        stats.put("total_books", repository.getBookCount());
        return stats;
    }
}