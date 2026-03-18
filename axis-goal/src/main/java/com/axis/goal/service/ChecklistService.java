package com.axis.goal.service;

import com.axis.goal.model.dto.*;

import java.util.List;
import java.util.UUID;

public interface ChecklistService {

    ChecklistResponse createChecklist(ChecklistRequest request);

    List<ChecklistResponse> findAllChecklists(UUID ownerId);

    ChecklistResponse patchChecklist(UUID checklistId, ChecklistRequest request);

    void deleteChecklist(UUID checklistId);

    ChecklistItemResponse createItem(UUID checklistId, ChecklistItemRequest request);

    ChecklistItemResponse patchItem(UUID checklistId, UUID itemId, ChecklistItemRequest request);

    ChecklistItemResponse reorderItem(UUID checklistId, UUID itemId, int newPosition);

    void deleteItem(UUID checklistId, UUID itemId);
}
