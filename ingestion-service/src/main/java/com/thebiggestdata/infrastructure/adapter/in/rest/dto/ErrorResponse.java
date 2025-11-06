package com.thebiggestdata.infrastructure.adapter.in.rest.dto;


public record ErrorResponse(
        int status,
        String error,
        String message,
        String path
) {}
