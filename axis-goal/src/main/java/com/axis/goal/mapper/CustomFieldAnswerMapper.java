package com.axis.goal.mapper;

import com.axis.goal.model.dto.CustomFieldAnswerRequest;
import com.axis.goal.model.dto.CustomFieldAnswerResponse;
import com.axis.goal.model.entity.CustomFieldAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomFieldAnswerMapper {

    /**
     * Convert CustomFieldAnswer entity to CustomFieldAnswerResponse DTO
     * Maps field definition properties to response for easy frontend access
     */
    @Mapping(target = "fieldDefinitionId", source = "fieldDefinition.id")
    @Mapping(target = "fieldKey", source = "fieldDefinition.key")
    @Mapping(target = "fieldLabel", source = "fieldDefinition.label")
    @Mapping(target = "fieldType", source = "fieldDefinition.type")
    CustomFieldAnswerResponse toResponse(CustomFieldAnswer answer);

    /**
     * Convert CustomFieldAnswerRequest DTO to CustomFieldAnswer entity
     * Note: fieldDefinition and goal will be set separately in the service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fieldDefinition", ignore = true)
    @Mapping(target = "goal", ignore = true)
    CustomFieldAnswer toEntity(CustomFieldAnswerRequest request);

    /**
     * Update existing CustomFieldAnswer entity from CustomFieldAnswerRequest DTO
     * Note: Preserves id, fieldDefinition, and goal
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fieldDefinition", ignore = true)
    @Mapping(target = "goal", ignore = true)
    void updateEntity(CustomFieldAnswerRequest request, @MappingTarget CustomFieldAnswer answer);
}