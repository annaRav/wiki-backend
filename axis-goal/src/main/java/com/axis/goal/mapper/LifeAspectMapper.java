package com.axis.goal.mapper;

import com.axis.goal.model.dto.LifeAspectRequest;
import com.axis.goal.model.dto.LifeAspectResponse;
import com.axis.goal.model.entity.LifeAspect;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "cdi", uses = {LabelMapper.class})
public interface LifeAspectMapper {

    LifeAspectResponse toResponse(LifeAspect lifeAspect);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "labels", ignore = true)
    LifeAspect toEntity(LifeAspectRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "ratedStatus", nullValuePropertyMappingStrategy = IGNORE)
    void patchEntity(LifeAspectRequest request, @MappingTarget LifeAspect lifeAspect);
}
