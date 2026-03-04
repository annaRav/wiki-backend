package com.axis.goal.mapper;

import com.axis.goal.model.dto.ChecklistItemRequest;
import com.axis.goal.model.dto.ChecklistItemResponse;
import com.axis.goal.model.entity.ChecklistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "cdi")
public interface ChecklistItemMapper {

    ChecklistItemResponse toResponse(ChecklistItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checklist", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "completed", ignore = true)
    ChecklistItem toEntity(ChecklistItemRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checklist", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "completed", nullValuePropertyMappingStrategy = IGNORE)
    void patchEntity(ChecklistItemRequest request, @MappingTarget ChecklistItem item);
}
