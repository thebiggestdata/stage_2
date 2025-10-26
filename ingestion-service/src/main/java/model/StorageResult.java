package model;


public record StorageResult(
        boolean success,
        String headerPath,
        String bodyPath,
        String timestamp
)
{}
