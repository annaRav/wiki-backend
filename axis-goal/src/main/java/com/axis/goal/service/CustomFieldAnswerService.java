package com.axis.goal.service;

import com.axis.goal.model.dto.CustomFieldAnswerRequest;
import com.axis.goal.model.dto.CustomFieldAnswerResponse;

import java.util.List;
import java.util.UUID;

public interface CustomFieldAnswerService {

    CustomFieldAnswerResponse create(CustomFieldAnswerRequest request);

    CustomFieldAnswerResponse update(UUID id, CustomFieldAnswerRequest request);

    CustomFieldAnswerResponse patch(UUID id, CustomFieldAnswerRequest request);

    CustomFieldAnswerResponse findById(UUID id);

    List<CustomFieldAnswerResponse> findByOwnerId(UUID ownerId);

    void delete(UUID id);
}
