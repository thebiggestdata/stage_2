package com.thebiggestdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.thebiggestdata.controller.model.IngestionResponse;
import com.thebiggestdata.controller.model.IndexingResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ServiceClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String ingestionServiceUrl;
    private final String indexingServiceUrl;

    public ServiceClient(String ingestionServiceUrl, String indexingServiceUrl) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.ingestionServiceUrl = ingestionServiceUrl;
        this.indexingServiceUrl = indexingServiceUrl;
    }

    public ServiceClient() {
        this("http://localhost:8080", "http://localhost:8081");
    }

    public IngestionResponse downloadBook(int bookId) {
        try {
            String url = ingestionServiceUrl + "/api/v1/ingest/" + bookId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMinutes(2))
                    .build();

            System.out.println("[INGESTION] Downloading book " + bookId + "...");

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                IngestionResponse ingestionResponse = objectMapper.readValue(
                        response.body(),
                        IngestionResponse.class
                );

                if (ingestionResponse.isSuccess()) {
                    System.out.println("[INGESTION] ✓ Book " + bookId + " - Status: " + ingestionResponse.status());
                    return ingestionResponse;
                } else {
                    System.err.println("[INGESTION] ✗ Download failed for book " + bookId);
                    System.err.println("            Status: " + ingestionResponse.status());
                    return null;
                }
            } else {
                System.err.println("[INGESTION] ✗ HTTP " + response.statusCode() + " for book " + bookId);
                return null;
            }

        } catch (Exception e) {
            System.err.println("[INGESTION] ✗ Error downloading book " + bookId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public IndexingResponse indexBook(int bookId, String downloadDate, String downloadHour) {
        try {
            String url = String.format(
                    "%s/api/v1/indexing-service/index/update/%d?downloadDate=%s&downloadHour=%s",
                    indexingServiceUrl, bookId, downloadDate, downloadHour
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMinutes(5))
                    .build();

            System.out.println("[INDEXING] Indexing book " + bookId + "...");

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                IndexingResponse indexingResponse = objectMapper.readValue(
                        response.body(),
                        IndexingResponse.class
                );

                if (indexingResponse.isSuccess()) {
                    System.out.println("[INDEXING] ✓ Book " + bookId + " indexed successfully");
                    return indexingResponse;
                } else {
                    System.err.println("[INDEXING] ✗ Indexing failed for book " + bookId);
                    System.err.println("            Status: " + indexingResponse.status());
                    System.err.println("            Message: " + indexingResponse.message());
                    return null;
                }
            } else {
                System.err.println("[INDEXING] ✗ HTTP " + response.statusCode() + " for book " + bookId);
                System.err.println("            Response: " + response.body());
                return null;
            }

        } catch (Exception e) {
            System.err.println("[INDEXING] ✗ Error indexing book " + bookId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean processBook(int bookId) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PROCESSING BOOK " + bookId);
        System.out.println("=".repeat(60));

        IngestionResponse ingestionResponse = downloadBook(bookId);
        if (ingestionResponse == null) {
            System.err.println("✗ Failed to download book " + bookId);
            return false;
        }

        String date = ingestionResponse.getDate();
        String hour = ingestionResponse.getHour();

        if (date == null || hour == null) {
            System.err.println("✗ Invalid timestamp format: " + ingestionResponse.getTimestamp());
            return false;
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        IndexingResponse indexingResponse = indexBook(bookId, date, hour);
        if (indexingResponse == null || !indexingResponse.isSuccess()) {
            System.err.println("✗ Failed to index book " + bookId);
            return false;
        }

        System.out.println("✓ Book " + bookId + " processed successfully");
        return true;
    }
}