package com.axis.media.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MediaFileResponse(
    UUID id,
    UUID ownerId,
    String originalFilename,
    String mimeType,
    long sizeBytes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
