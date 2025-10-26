package com.example.search.service;

import com.example.search.config.MongoConfig;
import com.example.search.model.BookInfo;
import com.example.search.model.SearchFilters;
import com.example.search.model.SearchResult;
import com.example.search.repository.InvertedIndexRepository;
import com.example.search.repository.MetadataRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private final Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private MongoConfig mongoConfig;

    @Autowired
    private InvertedIndexRepository invertedIndexRepository;

    @Autowired
    private MetadataRepository metadataRepository;

    @PostConstruct
    public void initialize() {
        logger.info("Initializing SearchService...");

        invertedIndexRepository.initialize(
                mongoConfig.getMongoUri(),
                mongoConfig.getInvertedIndexDatabase(),
                mongoConfig.getInvertedIndexCollection()
        );

        metadataRepository.initialize(
                mongoConfig.getMongoUri(),
                mongoConfig.getMetadataDatabase(),
                mongoConfig.getMetadataCollection()
        );

        logger.info("SearchService initialized successfully");
    }

    public SearchResult search(String query, SearchFilters filters) {
        logger.info("Search request - query: '{}', filters: {}", query, filtersToString(filters));

        Set<Integer> bookIds = invertedIndexRepository.findBookIdsByTerm(query);

        if (bookIds.isEmpty()) {
            logger.info("No books found for query '{}'", query);
            return new SearchResult(query, buildFiltersMap(filters), 0, List.of());
        }

        logger.debug("Found {} candidate books from inverted index", bookIds.size());

        List<BookInfo> candidateBooks = metadataRepository.findBooksByIds(bookIds);
        List<BookInfo> filteredBooks = applyFilters(candidateBooks, filters);

        filteredBooks.sort(Comparator.comparingInt(BookInfo::getBookId));

        logger.info("Search completed - query: '{}', results: {}", query, filteredBooks.size());

        return new SearchResult(
                query,
                buildFiltersMap(filters),
                filteredBooks.size(),
                filteredBooks
        );
    }

    private List<BookInfo> applyFilters(List<BookInfo> books, SearchFilters filters) {
        if (filters == null || !filters.hasAnyFilter()) {
            return books;
        }

        return books.stream()
                .filter(book -> matchesFilters(book, filters))
                .collect(Collectors.toList());
    }

    private boolean matchesFilters(BookInfo book, SearchFilters filters) {
        if (filters.hasAuthor()) {
            if (book.getAuthor() == null ||
                    !book.getAuthor().toLowerCase().contains(filters.getAuthor().toLowerCase())) {
                return false;
            }
        }

        if (filters.hasLanguage()) {
            if (book.getLanguage() == null ||
                    !book.getLanguage().equalsIgnoreCase(filters.getLanguage())) {
                return false;
            }
        }

        if (filters.hasYear()) {
            if (book.getYear() == null || !book.getYear().equals(filters.getYear())) {
                return false;
            }
        }

        return true;
    }

    private Map<String, Object> buildFiltersMap(SearchFilters filters) {
        Map<String, Object> filtersMap = new HashMap<>();

        if (filters != null) {
            if (filters.hasAuthor()) filtersMap.put("author", filters.getAuthor());
            if (filters.hasLanguage()) filtersMap.put("language", filters.getLanguage());
            if (filters.hasYear()) filtersMap.put("year", filters.getYear());
        }

        return filtersMap;
    }

    private String filtersToString(SearchFilters filters) {
        if (filters == null || !filters.hasAnyFilter()) {
            return "none";
        }

        List<String> parts = new ArrayList<>();
        if (filters.hasAuthor()) parts.add("author=" + filters.getAuthor());
        if (filters.hasLanguage()) parts.add("language=" + filters.getLanguage());
        if (filters.hasYear()) parts.add("year=" + filters.getYear());

        return String.join(", ", parts);
    }

    @PreDestroy
    public void cleanup() {
        logger.info("Shutting down SearchService...");
        invertedIndexRepository.close();
        metadataRepository.close();
        logger.info("SearchService shut down successfully");
    }
}