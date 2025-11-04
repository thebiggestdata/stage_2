package com.thebiggestdata.ingestion.model;

public record Book (
        int bookId,
        String header,
        String body,
        String footer
)
{}
