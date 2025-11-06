package com.thebiggestdata.application.dto;


public record HealthStatus(
        String status,
        String service,
        String version,
        long timestamp
) {}

