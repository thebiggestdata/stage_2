package com.thebiggestdata.infrastructure.adapter.in.rest.dto;


public record HealthResponse(
        String status,
        String service,
        String version,
        long timestamp
) {}
