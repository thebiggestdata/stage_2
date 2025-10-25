package com.example.search.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResponse {
    private String query;
    private Map<String, Object> filters;
    private int count;
    private List<Book> results;

    public SearchResponse(String query, List<Book> results) {
        this.query = query;
        this.filters = new HashMap<>();
        this.results = results;
        this.count = results != null ? results.size() : 0;
    }

    public SearchResponse(String query, Map<String, Object> filters, List<Book> results) {
        this.query = query;
        this.filters = filters != null ? new HashMap<>(filters) : new HashMap<>();
        this.results = results;
        this.count = results != null ? results.size() : 0;
    }

    public String getQuery() { return query; }
    public Map<String, Object> getFilters() { return filters; }
    public int getCount() { return count; }
    public List<Book> getResults() { return results; }

    public void setQuery(String query) { this.query = query; }
    public void setFilters(Map<String, Object> filters) { this.filters = filters; }
    public void setCount(int count) { this.count = count; }
    public void setResults(List<Book> results) {
        this.results = results;
        this.count = results != null ? results.size() : 0;
    }

    public void addFilter(String key, Object value) {
        if (value != null) {
            this.filters.put(key, value);
        }
    }
}