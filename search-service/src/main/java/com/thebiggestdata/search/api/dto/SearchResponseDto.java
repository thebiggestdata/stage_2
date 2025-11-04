package com.thebiggestdata.search.api.dto;

import com.thebiggestdata.search.model.BookInfo;
import java.util.List;
import java.util.Map;

public record SearchResponseDto(
        String query,
        Map<String, Object> filters,
        int count,
        List<BookInfo> results
) {}