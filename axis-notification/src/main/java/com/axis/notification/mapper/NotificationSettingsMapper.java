package com.axis.notification.mapper;

import com.axis.notification.model.dto.NotificationSettingsRequest;
import com.axis.notification.model.dto.NotificationSettingsResponse;
import com.axis.notification.model.entity.NotificationSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface NotificationSettingsMapper {

    /**
     * Convert request DTO to entity
     * ID and userId are set by the service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    NotificationSettings toEntity(NotificationSettingsRequest request);

    /**
     * Convert entity to response DTO
     */
    NotificationSettingsResponse toResponse(NotificationSettings entity);

    /**
     * Update existing entity from request DTO
     * ID, userId, and timestamps are preserved
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(NotificationSettingsRequest request, @MappingTarget NotificationSettings entity);
}