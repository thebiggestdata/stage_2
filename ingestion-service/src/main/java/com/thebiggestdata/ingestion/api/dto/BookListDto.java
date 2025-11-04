package com.thebiggestdata.ingestion.api.dto;

import java.util.List;

public record BookListDto (
    int count,
    List<Integer> bookIds
)
{ }
