package com.thebiggestdata.application.config;

public record CrawlerConfig(
        int startId,
        int endId,
        long delay
) {
    public int getTotalBooks() {
        return endId - startId + 1;
    }
}
