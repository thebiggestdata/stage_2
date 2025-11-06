package com.thebiggestdata.application.dto;


public record BulkIngestionCommand(
        int startId,
        int endId,
        long delayMs
) {
    public BulkIngestionCommand(int startId, int endId, Long delayMs) {
        this(startId, endId, delayMs != null ? delayMs : 1000L);
    }
}

