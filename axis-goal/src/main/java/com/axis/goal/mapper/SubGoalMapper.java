package com.axis.goal.mapper;

import com.axis.goal.model.dto.SubGoalRequest;
import com.axis.goal.model.dto.SubGoalResponse;
import com.axis.goal.model.entity.SubGoal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "cdi", uses = {LabelMapper.class})
public interface SubGoalMapper {

    @Mapping(target = "goalId", source = "goal.id")
    SubGoalResponse toResponse(SubGoal subGoal);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "labels", ignore = true)

    SubGoal toEntity(SubGoalRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "labels", ignore = true)

    @Mapping(target = "title", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "status", nullValuePropertyMappingStrategy = IGNORE)
    void patchEntity(SubGoalRequest request, @MappingTarget SubGoal subGoal);
}
