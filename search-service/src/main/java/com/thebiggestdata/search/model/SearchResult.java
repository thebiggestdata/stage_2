package com.thebiggestdata.search.model;

import java.util.List;
import java.util.Map;

public record SearchResult(
        String query,
        Map<String, Object> filters,
        int count,
        List<com.thebiggestdata.search.model.BookInfo> results
) {}