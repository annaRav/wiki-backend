package com.wiki.media.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@Tag(name = "Health Check", description = "Health check endpoints for wiki-media service")
public class HealthCheckController {

    @GetMapping("/health")
    @Operation(
        summary = "Health check endpoint",
        description = "Returns the health status of the wiki-media service"
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "service", "wiki-media",
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "database", "MongoDB",
            "version", "1.0.0-SNAPSHOT"
        ));
    }

    @GetMapping("/ping")
    @Operation(
        summary = "Ping endpoint",
        description = "Simple ping endpoint to check if service is responding"
    )
    @ApiResponse(responseCode = "200", description = "Service is responding")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}