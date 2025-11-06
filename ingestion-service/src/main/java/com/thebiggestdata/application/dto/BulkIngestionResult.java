package com.thebiggestdata.application.dto;


public record BulkIngestionResult(
        int startId,
        int endId,
        int totalRequested,
        int successfulCount,
        int failedCount,
        String status,
        String message
) {}

