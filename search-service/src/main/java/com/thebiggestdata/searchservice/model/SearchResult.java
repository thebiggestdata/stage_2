package com.thebiggestdata.searchservice.model;

import java.util.List;
import java.util.Map;

public record SearchResult(
        String query,
        Map<String, Object> filters,
        int count,
        List<com.thebiggestdata.searchservice.model.BookInfo> results
) {}