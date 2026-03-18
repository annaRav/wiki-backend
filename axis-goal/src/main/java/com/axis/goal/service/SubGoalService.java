package com.axis.goal.service;

import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.model.dto.SubGoalRequest;
import com.axis.goal.model.dto.SubGoalResponse;

import java.util.UUID;

public interface SubGoalService {

    SubGoalResponse create(SubGoalRequest request);

    SubGoalResponse patch(UUID id, SubGoalRequest request);

    SubGoalResponse findById(UUID id);

    PageResponse<SubGoalResponse> findAll(int page, int size, String sortBy, String sortDirection);

    PageResponse<SubGoalResponse> findByGoalId(UUID goalId, int page, int size, String sortBy, String sortDirection);

    void delete(UUID id);
}
