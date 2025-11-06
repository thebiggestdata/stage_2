package com.thebiggestdata.application.service;

import com.thebiggestdata.application.dto.HealthStatus;


public class HealthCheckApplicationService {
    private static final String SERVICE_NAME = "book-ingestion-service";
    private static final String VERSION = "1.0.0";

    public HealthStatus getHealth() {
        return new HealthStatus(
                "UP",
                SERVICE_NAME,
                VERSION,
                System.currentTimeMillis()
        );
    }

    public boolean isReady() {
        //todo: possible check before true
        return true;
    }

    public boolean isAlive() {
        return true;
    }
}

