package com.thebiggestdata.infrastructure.adapter.in.rest.dto;

import com.google.gson.annotations.SerializedName;


public record StatusResponse(
        @SerializedName("book_id") int bookId,
        String status
) {}

