package com.thebiggestdata.searchservice.model;

import java.util.List;
import java.util.Map;

public class SearchResult {
    private String query;
    private Map<String, Object> filters;
    private int count;
    private List<BookInfo> results;

    public SearchResult() {}

    public SearchResult(String query, Map<String, Object> filters, int count, List<BookInfo> results) {
        this.query = query;
        this.filters = filters;
        this.count = count;
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<BookInfo> getResults() {
        return results;
    }

    public void setResults(List<BookInfo> results) {
        this.results = results;
    }
}
