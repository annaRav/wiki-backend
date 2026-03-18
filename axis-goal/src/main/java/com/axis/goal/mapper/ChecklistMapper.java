package com.axis.goal.mapper;

import com.axis.goal.model.dto.ChecklistRequest;
import com.axis.goal.model.dto.ChecklistResponse;
import com.axis.goal.model.entity.Checklist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "cdi", uses = {ChecklistItemMapper.class})
public interface ChecklistMapper {

    ChecklistResponse toResponse(Checklist checklist);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "ownerType", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Checklist toEntity(ChecklistRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "ownerType", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = IGNORE)
    void patchEntity(ChecklistRequest request, @MappingTarget Checklist checklist);
}
