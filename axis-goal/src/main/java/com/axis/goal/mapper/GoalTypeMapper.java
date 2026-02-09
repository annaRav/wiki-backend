package com.axis.goal.mapper;

import com.axis.goal.model.dto.CustomFieldDefinitionRequest;
import com.axis.goal.model.dto.CustomFieldDefinitionResponse;
import com.axis.goal.model.dto.GoalTypeRequest;
import com.axis.goal.model.dto.GoalTypeResponse;
import com.axis.goal.model.entity.CustomFieldDefinition;
import com.axis.goal.model.entity.GoalType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface GoalTypeMapper {

    /**
     * Converts GoalType entity to Response DTO
     */
    GoalTypeResponse toResponse(GoalType goalType);

    /**
     * Converts Request DTO to GoalType entity
     * Ignore technical fields set in service or database
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "levelNumber", ignore = true)
    @Mapping(target = "goals", ignore = true)
    GoalType toEntity(GoalTypeRequest request);

    /**
     * Updates existing GoalType entity from Request DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "levelNumber", ignore = true)
    @Mapping(target = "goals", ignore = true)
    void updateEntity(GoalTypeRequest request, @MappingTarget GoalType goalType);

    /**
     * Mapping for custom field definitions (used automatically by MapStruct for lists)
     */
    CustomFieldDefinitionResponse toFieldResponse(CustomFieldDefinition field);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "goalType", ignore = true)
    CustomFieldDefinition toFieldEntity(CustomFieldDefinitionRequest request);
}
