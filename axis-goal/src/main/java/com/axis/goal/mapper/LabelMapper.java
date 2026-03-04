package com.axis.goal.mapper;

import com.axis.goal.model.dto.LabelRequest;
import com.axis.goal.model.dto.LabelResponse;
import com.axis.goal.model.entity.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "cdi")
public interface LabelMapper {

    LabelResponse toResponse(Label label);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "goals", ignore = true)
    Label toEntity(LabelRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "displayName", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "color", nullValuePropertyMappingStrategy = IGNORE)
    void patchEntity(LabelRequest request, @MappingTarget Label label);
}