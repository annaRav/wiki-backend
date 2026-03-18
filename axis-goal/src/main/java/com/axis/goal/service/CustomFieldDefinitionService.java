package com.axis.goal.service;

import com.axis.goal.model.dto.CustomFieldDefinitionRequest;
import com.axis.goal.model.dto.CustomFieldDefinitionResponse;
import com.axis.goal.model.enums.OwnerType;

import java.util.List;
import java.util.UUID;

public interface CustomFieldDefinitionService {

    CustomFieldDefinitionResponse create(CustomFieldDefinitionRequest request);

    CustomFieldDefinitionResponse update(UUID id, CustomFieldDefinitionRequest request);

    CustomFieldDefinitionResponse patch(UUID id, CustomFieldDefinitionRequest request);

    CustomFieldDefinitionResponse findById(UUID id);

    List<CustomFieldDefinitionResponse> findByOwnerType(OwnerType ownerType);

    void delete(UUID id);
}
