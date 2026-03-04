package com.axis.user.mapper;

import com.axis.user.model.dto.UserSocialLinksRequest;
import com.axis.user.model.dto.UserSocialLinksResponse;
import com.axis.user.model.entity.UserSocialLinks;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "jakarta", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserSocialLinksMapper {

    UserSocialLinksResponse toResponse(UserSocialLinks entity);

    void updateEntity(UserSocialLinksRequest request, @MappingTarget UserSocialLinks entity);
}