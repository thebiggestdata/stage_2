package com.thebiggestdata.infrastructure.adapter.in.cli;

import com.thebiggestdata.application.config.CrawlerConfig;
import com.thebiggestdata.application.config.DependencyFactory;
import com.thebiggestdata.infrastructure.adapter.in.CrawlerController;


public class CliAdapter {
    private static final int DEFAULT_START_ID = 1;
    private static final int DEFAULT_END_ID = 10;
    private static final long DEFAULT_DELAY = 1000L;
    private static final String DEFAULT_DATALAKE_PATH = "datalake/";

    public void run(String[] args) {
        CrawlerConfig config = parseConfiguration(args);
        String datalakePath = parseDatalakePath(args);
        CrawlerController controller = DependencyFactory.createCrawlerController(config, datalakePath);
        controller.crawlRange();
    }

    private CrawlerConfig parseConfiguration(String[] args) {
        int startId = parseIntArgument(args, 0, DEFAULT_START_ID);
        int endId = parseIntArgument(args, 1, DEFAULT_END_ID);
        long delay = parseLongArgument(args, 2, DEFAULT_DELAY);
        return new CrawlerConfig(startId, endId, delay);
    }

    private String parseDatalakePath(String[] args) {
        return args.length > 3 ? args[3] : DEFAULT_DATALAKE_PATH;
    }

    private int parseIntArgument(String[] args, int index, int defaultValue) {
        if (args.length <= index) return defaultValue;
        try {return Integer.parseInt(args[index]);}
        catch (NumberFormatException e) {
            System.err.println("Invalid number at position " + index + ", using default: " + defaultValue);
            return defaultValue;
        }
    }

    private long parseLongArgument(String[] args, int index, long defaultValue) {
        if (args.length <= index) return defaultValue;
        try {return Long.parseLong(args[index]);}
        catch (NumberFormatException e) {
            System.err.println("Invalid number at position " + index + ", using default: " + defaultValue);
            return defaultValue;
        }
    }
}

