package com.thebiggestdata.infrastructure.adapter.in.rest.dto;

import com.google.gson.annotations.SerializedName;


public record BulkIngestionResponse(
        @SerializedName("start_id") int startId,
        @SerializedName("end_id") int endId,
        @SerializedName("total_requested") int totalRequested,
        @SerializedName("successful_count") int successfulCount,
        @SerializedName("failed_count") int failedCount,
        String status,
        String message
) {}
