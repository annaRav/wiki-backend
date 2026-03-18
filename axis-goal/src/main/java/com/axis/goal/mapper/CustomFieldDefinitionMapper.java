package com.axis.goal.mapper;

import com.axis.goal.model.dto.CustomFieldDefinitionRequest;
import com.axis.goal.model.dto.CustomFieldDefinitionResponse;
import com.axis.goal.model.entity.CustomFieldDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "cdi")
public interface CustomFieldDefinitionMapper {

    CustomFieldDefinitionResponse toResponse(CustomFieldDefinition definition);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    CustomFieldDefinition toEntity(CustomFieldDefinitionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateEntity(CustomFieldDefinitionRequest request, @MappingTarget CustomFieldDefinition definition);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "label", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "type", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "placeholder", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "ownerType", nullValuePropertyMappingStrategy = IGNORE)
    void patchEntity(CustomFieldDefinitionRequest request, @MappingTarget CustomFieldDefinition definition);
}
