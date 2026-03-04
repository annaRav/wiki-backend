package com.axis.user.mapper;

import com.axis.user.model.dto.UserSettingsRequest;
import com.axis.user.model.dto.UserSettingsResponse;
import com.axis.user.model.entity.UserSettings;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "jakarta", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserSettingsMapper {

    UserSettingsResponse toResponse(UserSettings entity);

    void updateEntity(UserSettingsRequest request, @MappingTarget UserSettings entity);
}