package com.thebiggestdata.searchservice.service;

import com.thebiggestdata.searchservice.config.MongoConfig;
import com.thebiggestdata.searchservice.model.BookInfo;
import com.thebiggestdata.searchservice.model.SearchFilters;
import com.thebiggestdata.searchservice.model.SearchResult;
import com.thebiggestdata.searchservice.repository.InvertedIndexRepository;
import com.thebiggestdata.searchservice.repository.MetadataRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

        var bookIds = invertedIndexRepository.findBookIdsByTerm(query);

        if (bookIds.isEmpty()) {
            logger.info("No books found for query '{}'", query);
            return new SearchResult(query, buildFiltersMap(filters), 0, List.of());
        }

        logger.debug("Found {} candidate books from inverted index", bookIds.size());

        var candidateBooks = metadataRepository.findBooksByIds(bookIds);
        var filteredBooks = applyFilters(candidateBooks, filters);

        filteredBooks.sort(Comparator.comparingInt(BookInfo::bookId));

        logger.info("Search completed - query: '{}', results: {}", query, filteredBooks.size());

        return new SearchResult(
                query,
                buildFiltersMap(filters),
                filteredBooks.size(),
                filteredBooks
        );
    }

    public List<BookInfo> applyFilters(List<BookInfo> books, SearchFilters filters) {
        if (filters == null || !filters.hasAnyFilter()) {
            return books;
        }

        return books.stream()
                .filter(book -> matchesFilters(book, filters))
                .toList();
    }

    private boolean matchesFilters(BookInfo book, SearchFilters filters) {
        if (filters.hasAuthor()) {
            if (book.author() == null ||
                    !book.author().toLowerCase().contains(filters.author().toLowerCase())) {
                return false;
            }
        }

        if (filters.hasLanguage()) {
            if (book.language() == null ||
                    !book.language().equalsIgnoreCase(filters.language())) {
                return false;
            }
        }

        if (filters.hasYear()) {
            if (book.year() == null || !book.year().equals(filters.year())) {
                return false;
            }
        }

        return true;
    }

    private Map<String, Object> buildFiltersMap(SearchFilters filters) {
        var filtersMap = new HashMap<String, Object>();

        if (filters != null) {
            if (filters.hasAuthor()) filtersMap.put("author", filters.author());
            if (filters.hasLanguage()) filtersMap.put("language", filters.language());
            if (filters.hasYear()) filtersMap.put("year", filters.year());
        }

        return filtersMap;
    }

    private String filtersToString(SearchFilters filters) {
        if (filters == null || !filters.hasAnyFilter()) {
            return "none";
        }

        var parts = new ArrayList<String>();
        if (filters.hasAuthor()) parts.add("author=" + filters.author());
        if (filters.hasLanguage()) parts.add("language=" + filters.language());
        if (filters.hasYear()) parts.add("year=" + filters.year());

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