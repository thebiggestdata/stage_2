package com.thebiggestdata.domain.model;


public record Book (
        int bookId,
        String header,
        String body,
        String footer
)
{}
