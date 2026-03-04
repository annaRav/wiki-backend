package com.axis.user.mapper;

import com.axis.user.model.dto.UserStatsResponse;
import com.axis.user.model.entity.UserStats;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta")
public interface UserStatsMapper {

    UserStatsResponse toResponse(UserStats entity);
}