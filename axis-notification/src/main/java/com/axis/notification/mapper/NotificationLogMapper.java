package com.axis.notification.mapper;

import com.axis.notification.model.dto.NotificationLogRequest;
import com.axis.notification.model.dto.NotificationLogResponse;
import com.axis.notification.model.entity.NotificationLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface NotificationLogMapper {

    /**
     * Convert request DTO to entity
     * ID and userId are set by the service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    NotificationLog toEntity(NotificationLogRequest request);

    /**
     * Convert entity to response DTO
     */
    NotificationLogResponse toResponse(NotificationLog entity);

    /**
     * Update existing entity from request DTO
     * ID, userId, and timestamps are preserved
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(NotificationLogRequest request, @MappingTarget NotificationLog entity);
}