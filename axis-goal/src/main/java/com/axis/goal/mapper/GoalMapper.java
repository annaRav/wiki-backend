package com.axis.goal.mapper;

import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.entity.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "cdi", uses = {CustomFieldAnswerMapper.class, LabelMapper.class})
public interface GoalMapper {

    /**
     * Convert Goal entity to GoalResponse DTO
     */
    @Mapping(target = "typeId", source = "type.id")
    GoalResponse toResponse(Goal goal);

    /**
     * Convert GoalRequest DTO to Goal entity
     * Note: userId, type, and labels will be set separately by the service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subGoals", ignore = true)
    @Mapping(target = "labels", ignore = true)
    Goal toEntity(GoalRequest request);

    /**
     * Update existing Goal entity from GoalRequest DTO
     * Note: Preserves id, userId, type, createdAt, updatedAt
     * parent, subGoals, and labels are managed through separate service logic
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subGoals", ignore = true)
    @Mapping(target = "labels", ignore = true)
    void updateEntity(GoalRequest request, @MappingTarget Goal goal);

    /**
     * Partially updates existing Goal entity from Request DTO (PATCH - partial update)
     * Only non-null fields in the request will be updated
     * Note: customAnswers and labels are ignored here - handled by the service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subGoals", ignore = true)
    @Mapping(target = "customAnswers", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "status", nullValuePropertyMappingStrategy = IGNORE)
    void patchEntity(GoalRequest request, @MappingTarget Goal goal);
}