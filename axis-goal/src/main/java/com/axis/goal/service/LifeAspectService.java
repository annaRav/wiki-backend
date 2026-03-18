package com.axis.goal.service;

import com.axis.goal.model.dto.LifeAspectRequest;
import com.axis.goal.model.dto.LifeAspectResponse;
import com.axis.goal.model.dto.PageResponse;

import java.util.UUID;

public interface LifeAspectService {

    LifeAspectResponse create(LifeAspectRequest request);

    LifeAspectResponse patch(UUID id, LifeAspectRequest request);

    LifeAspectResponse findById(UUID id);

    PageResponse<LifeAspectResponse> findAll(int page, int size, String sortBy, String sortDirection);

    void delete(UUID id);
}
