package com.thebiggestdata.infrastructure.adapter.in.rest.dto;

import com.google.gson.annotations.SerializedName;


public record BookIngestionRequest(
        @SerializedName("book_id") int bookId
) {}
