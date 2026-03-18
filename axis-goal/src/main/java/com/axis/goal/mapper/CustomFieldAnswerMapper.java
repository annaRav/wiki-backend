package com.axis.goal.mapper;

import com.axis.goal.model.dto.CustomFieldAnswerRequest;
import com.axis.goal.model.dto.CustomFieldAnswerResponse;
import com.axis.goal.model.entity.CustomFieldAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "cdi")
public interface CustomFieldAnswerMapper {

    @Mapping(target = "fieldDefinitionId", source = "fieldDefinition.id")
    @Mapping(target = "fieldLabel", source = "fieldDefinition.label")
    @Mapping(target = "fieldType", source = "fieldDefinition.type")
    CustomFieldAnswerResponse toResponse(CustomFieldAnswer answer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fieldDefinition", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    CustomFieldAnswer toEntity(CustomFieldAnswerRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fieldDefinition", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "value", nullValuePropertyMappingStrategy = IGNORE)
    void patchEntity(CustomFieldAnswerRequest request, @MappingTarget CustomFieldAnswer answer);
}
