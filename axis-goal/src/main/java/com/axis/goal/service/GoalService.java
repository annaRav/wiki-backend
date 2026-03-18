package com.axis.goal.service;

import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.model.enums.ProgressStatus;

import java.util.UUID;

public interface GoalService {

    GoalResponse create(GoalRequest request);

    GoalResponse patch(UUID id, GoalRequest request);

    GoalResponse findById(UUID id);

    PageResponse<GoalResponse> findAll(int page, int size, String sortBy, String sortDirection);

    PageResponse<GoalResponse> findByStatus(ProgressStatus status, int page, int size, String sortBy, String sortDirection);

    PageResponse<GoalResponse> findByLifeAspectId(UUID lifeAspectId, int page, int size, String sortBy, String sortDirection);

    void delete(UUID id);
}
