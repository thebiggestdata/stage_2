package com.thebiggestdata.searchservice.dto;

import com.thebiggestdata.searchservice.model.BookInfo;
import java.util.List;
import java.util.Map;

public record SearchResponseDto(
        String query,
        Map<String, Object> filters,
        int count,
        List<BookInfo> results
) {}