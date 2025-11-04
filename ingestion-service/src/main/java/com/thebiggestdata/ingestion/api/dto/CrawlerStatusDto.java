package com.thebiggestdata.ingestion.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CrawlerStatusDto(
        @JsonProperty("currentBookId")
        int currentBookId,
        @JsonProperty("startId")
        int startId,
        @JsonProperty("endId")
        int endId,
        @JsonProperty("delayMs")
        long delayMs,
        @JsonProperty("status")
        String status
) {}