package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StorageResultDto (
        @JsonProperty("success")
        boolean success,
        @JsonProperty("headerPath")
        String headerPath,
        @JsonProperty("bodyPath")
        String bodyPath,
        @JsonProperty("timestamp")
        String timestamp
        )
{}
