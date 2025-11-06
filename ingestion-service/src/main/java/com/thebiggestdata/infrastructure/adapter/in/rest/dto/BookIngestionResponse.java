package com.thebiggestdata.infrastructure.adapter.in.rest.dto;

import com.google.gson.annotations.SerializedName;


public record BookIngestionResponse(
        @SerializedName("book_id") int bookId,
        boolean success,
        String message,
        @SerializedName("header_path") String headerPath,
        @SerializedName("body_path") String bodyPath,
        String timestamp
) {}
