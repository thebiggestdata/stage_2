package com.thebiggestdata.infrastructure.adapter.in.rest.dto;

import java.util.List;


public record ListResponse(
        int count,
        List<Integer> books
) {}

