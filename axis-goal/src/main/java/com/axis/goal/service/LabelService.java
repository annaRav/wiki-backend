package com.axis.goal.service;

import com.axis.goal.model.dto.LabelRequest;
import com.axis.goal.model.dto.LabelResponse;

import java.util.List;
import java.util.UUID;

public interface LabelService {

    LabelResponse create(LabelRequest request);

    LabelResponse patch(UUID id, LabelRequest request);

    LabelResponse findById(UUID id);

    List<LabelResponse> findAll();

    void delete(UUID id);
}