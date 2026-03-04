package com.axis.goal.service;

import com.axis.goal.model.dto.*;

import java.util.List;
import java.util.UUID;

public interface ChecklistService {

    ChecklistResponse createChecklist(UUID goalId, ChecklistRequest request);

    List<ChecklistResponse> findAllChecklists(UUID goalId);

    ChecklistResponse patchChecklist(UUID goalId, UUID checklistId, ChecklistRequest request);

    void deleteChecklist(UUID goalId, UUID checklistId);

    ChecklistItemResponse createItem(UUID goalId, UUID checklistId, ChecklistItemRequest request);

    ChecklistItemResponse patchItem(UUID goalId, UUID checklistId, UUID itemId, ChecklistItemRequest request);

    ChecklistItemResponse reorderItem(UUID goalId, UUID checklistId, UUID itemId, int newPosition);

    void deleteItem(UUID goalId, UUID checklistId, UUID itemId);
}
