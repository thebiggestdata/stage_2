package com.thebiggestdata.controller;

public class ControlApp {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          CONTROL MODULE - THE BIGGEST DATA                ║");
        System.out.println("║              Search Engine Orchestrator                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        int startId = 1;
        int endId = 10;

        if (args.length >= 2) {
            try {
                startId = Integer.parseInt(args[0]);
                endId = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Error: Arguments must be integers");
                System.err.println("Usage: java -jar control-module.jar <start_id> <end_id>");
                System.exit(1);
            }
        } else if (args.length == 1) {
            System.err.println("Error: Please provide both start and end IDs");
            System.err.println("Usage: java -jar control-module.jar <start_id> <end_id>");
            System.exit(1);
        }

        if (startId <= 0 || endId < startId) {
            System.err.println("Error: Invalid range. start_id must be positive and end_id >= start_id");
            System.exit(1);
        }

        System.out.println("Configuration:");
        System.out.println("  Book range: " + startId + " to " + endId);
        System.out.println("  Total books: " + (endId - startId + 1));
        System.out.println();

        System.out.println("Checking services availability...");
        if (!checkServices()) {
            System.err.println("\n✗ Services are not available. Please start all services first:");
            System.err.println("  1. Ingestion Service (port 8080)");
            System.err.println("  2. Indexing Service (port 8081)");
            System.exit(1);
        }
        System.out.println("✓ All services are running\n");

        ServiceClient client = new ServiceClient();
        ProcessingTracker tracker = new ProcessingTracker();

        System.out.println("Previously processed books: " + tracker.getProcessedCount());
        System.out.println();

        long startTime = System.currentTimeMillis();
        int successCount = 0;
        int failureCount = 0;
        int skippedCount = 0;

        for (int bookId = startId; bookId <= endId; bookId++) {
            if (tracker.isProcessed(bookId)) {
                System.out.println("Book " + bookId + " already processed, skipping...");
                skippedCount++;
                continue;
            }

            boolean success = client.processBook(bookId);

            if (success) {
                tracker.markAsProcessed(bookId);
                successCount++;
            } else {
                failureCount++;
            }

            if (bookId < endId) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("\nProcessing interrupted by user");
                    break;
                }
            }
        }

        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;

        System.out.println("\n" + "=".repeat(60));
        System.out.println("PROCESSING SUMMARY");
        System.out.println("=".repeat(60));
        System.out.println("Total books requested:     " + (endId - startId + 1));
        System.out.println("Successfully processed:    " + successCount);
        System.out.println("Failed:                    " + failureCount);
        System.out.println("Skipped (already done):    " + skippedCount);
        System.out.println("Total time:                " + String.format("%.2f", elapsedSeconds) + " seconds");

        if (successCount > 0) {
            double avgTime = elapsedSeconds / successCount;
            System.out.println("Average time per book:     " + String.format("%.2f", avgTime) + " seconds");
        }

        System.out.println("=".repeat(60));

        if (failureCount > 0) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }

    private static boolean checkServices() {
        // Verificar puerto 8080 (Ingestion Service)
        if (!isPortOpen("localhost", 8080)) {
            System.err.println("✗ Ingestion Service not responding (port 8080)");
            return false;
        }
        System.out.println("  ✓ Ingestion Service (port 8080)");

        // Verificar puerto 8081 (Indexing Service)
        if (!isPortOpen("localhost", 8081)) {
            System.err.println("✗ Indexing Service not responding (port 8081)");
            return false;
        }
        System.out.println("  ✓ Indexing Service (port 8081)");

        return true;
    }

    private static boolean isPortOpen(String host, int port) {
        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), 5000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}