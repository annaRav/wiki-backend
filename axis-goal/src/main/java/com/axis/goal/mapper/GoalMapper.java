package com.axis.goal.mapper;

import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.entity.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CustomFieldAnswerMapper.class})
public interface GoalMapper {

    /**
     * Convert Goal entity to GoalResponse DTO
     */
    @Mapping(target = "typeId", source = "type.id")
    GoalResponse toResponse(Goal goal);

    /**
     * Convert GoalRequest DTO to Goal entity
     * Note: userId will be set separately from the authenticated user
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subGoals", ignore = true)
    Goal toEntity(GoalRequest request);

    /**
     * Update existing Goal entity from GoalRequest DTO
     * Note: Preserves id, userId, createdAt, updatedAt
     * parent and subGoals are managed through separate endpoints
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subGoals", ignore = true)
    void updateEntity(GoalRequest request, @MappingTarget Goal goal);
}