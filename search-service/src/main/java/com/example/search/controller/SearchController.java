package com.example.search.controller;

import com.example.search.model.Book;
import com.example.search.model.SearchQuery;
import com.example.search.model.SearchResponse;
import com.example.search.service.SearchService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    public void handleSearch(Context ctx) {
        try {
            String query = ctx.queryParam("q");
            String author = ctx.queryParam("author");
            String language = ctx.queryParam("language");
            String yearStr = ctx.queryParam("year");

            if (query == null || query.isBlank()) {
                sendError(ctx, 400, "Query parameter 'q' is required");
                return;
            }

            Integer year = parseYear(yearStr);
            if (yearStr != null && !yearStr.isBlank() && year == null) {
                sendError(ctx, 400, "Invalid year format");
                return;
            }

            SearchQuery searchQuery = new SearchQuery(query, author, language, year);
            List<Book> results = searchService.search(searchQuery);

            Map<String, Object> filters = buildFiltersMap(author, language, year);
            SearchResponse response = new SearchResponse(query, filters, results);

            ctx.json(response);

            logger.info("Search completed: query='{}', filters={}, results={}",
                    query, filters.size(), results.size());

        } catch (Exception e) {
            logger.error("Error processing search request", e);
            sendError(ctx, 500, "Internal server error: " + e.getMessage());
        }
    }

    public void handleRefresh(Context ctx) {
        try {
            searchService.refreshIndex();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Index refreshed successfully");

            ctx.json(response);
            logger.info("Index refresh completed");

        } catch (Exception e) {
            logger.error("Error refreshing index", e);
            sendError(ctx, 500, "Failed to refresh index: " + e.getMessage());
        }
    }

    public void handleStats(Context ctx) {
        try {
            Map<String, Object> stats = searchService.getStats();
            ctx.json(stats);

        } catch (Exception e) {
            logger.error("Error getting stats", e);
            sendError(ctx, 500, "Failed to get stats: " + e.getMessage());
        }
    }

    public void handleStatus(Context ctx) {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "search-service");
        status.put("status", "running");
        status.put("timestamp", System.currentTimeMillis());

        ctx.json(status);
    }

    private Integer parseYear(String yearStr) {
        if (yearStr == null || yearStr.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Map<String, Object> buildFiltersMap(String author, String language, Integer year) {
        Map<String, Object> filters = new HashMap<>();

        if (author != null && !author.isBlank()) {
            filters.put("author", author);
        }
        if (language != null && !language.isBlank()) {
            filters.put("language", language);
        }
        if (year != null) {
            filters.put("year", year);
        }

        return filters;
    }

    private void sendError(Context ctx, int statusCode, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        ctx.status(statusCode).json(error);
    }
}