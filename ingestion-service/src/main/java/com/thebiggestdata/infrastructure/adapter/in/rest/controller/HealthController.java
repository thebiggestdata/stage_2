package com.thebiggestdata.infrastructure.adapter.in.rest.controller;

import com.thebiggestdata.application.service.HealthCheckApplicationService;
import com.thebiggestdata.infrastructure.adapter.in.rest.mapper.RestDtoMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;


public class HealthController {
    private final HealthCheckApplicationService applicationService;

    public HealthController(HealthCheckApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public void health(Context ctx) {
        var status = applicationService.getHealth();
        var response = RestDtoMapper.toRestDto(status);
        ctx.status(HttpStatus.OK).json(response);
    }

    public void ready(Context ctx) {
        boolean isReady = applicationService.isReady();
        if (isReady) ctx.status(HttpStatus.OK).result("READY");
        else ctx.status(HttpStatus.SERVICE_UNAVAILABLE).result("NOT_READY");
    }

    public void live(Context ctx) {
        boolean isAlive = applicationService.isAlive();
        if (isAlive) ctx.status(HttpStatus.OK).result("ALIVE");
        else ctx.status(HttpStatus.SERVICE_UNAVAILABLE).result("NOT_ALIVE");
    }
}
