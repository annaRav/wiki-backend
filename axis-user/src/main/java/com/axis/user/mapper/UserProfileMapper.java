package com.axis.user.mapper;

import com.axis.user.model.dto.UserProfileRequest;
import com.axis.user.model.dto.UserProfileResponse;
import com.axis.user.model.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "jakarta", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {

    UserProfileResponse toResponse(UserProfile entity);

    void updateEntity(UserProfileRequest request, @MappingTarget UserProfile entity);
}