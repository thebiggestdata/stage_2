package model;

import java.time.LocalDateTime;

public record StorageResult(
        boolean success,
        String headerPath,
        String bodyPath,
        LocalDateTime timestamp
)
{}
