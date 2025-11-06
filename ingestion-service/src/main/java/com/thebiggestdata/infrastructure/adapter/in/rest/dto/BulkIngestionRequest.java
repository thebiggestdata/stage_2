package com.thebiggestdata.infrastructure.adapter.in.rest.dto;

import com.google.gson.annotations.SerializedName;


public record BulkIngestionRequest(
        @SerializedName("start_id") int startId,
        @SerializedName("end_id") int endId,
        @SerializedName("delay_ms") Long delayMs
) {
    public BulkIngestionRequest {
        if (delayMs == null) delayMs = 1000L;
    }
}
