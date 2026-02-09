package com.axis.goal.mapper;

import com.axis.goal.model.dto.CustomFieldDefinitionRequest;
import com.axis.goal.model.dto.CustomFieldDefinitionResponse;
import com.axis.goal.model.entity.CustomFieldDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface CustomFieldDefinitionMapper {

    /**
     * Convert CustomFieldDefinition entity to CustomFieldDefinitionResponse DTO
     */
    CustomFieldDefinitionResponse toResponse(CustomFieldDefinition definition);

    /**
     * Convert CustomFieldDefinitionRequest DTO to CustomFieldDefinition entity
     * Note: goalType will be set separately in the service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "goalType", ignore = true)
    CustomFieldDefinition toEntity(CustomFieldDefinitionRequest request);

    /**
     * Update existing CustomFieldDefinition entity from CustomFieldDefinitionRequest DTO
     * Note: Preserves id and goalType
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "goalType", ignore = true)
    void updateEntity(CustomFieldDefinitionRequest request, @MappingTarget CustomFieldDefinition definition);
}