package control.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatalakePathBuilder {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH");

    private final String basePath;

    public DatalakePathBuilder(String basePath) {
        this.basePath = basePath.endsWith("/") ? basePath : basePath + "/";
    }

    public String buildPath(LocalDateTime timestamp) {
        String dateStr = timestamp.format(DATE_FORMATTER);
        String hourStr = timestamp.format(HOUR_FORMATTER);
        return basePath + dateStr + "/" + hourStr + "/";
    }

    public String buildPath() {
        return buildPath(LocalDateTime.now());
    }
}