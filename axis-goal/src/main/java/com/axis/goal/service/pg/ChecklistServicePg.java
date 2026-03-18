package com.axis.goal.service.pg;

import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.ChecklistItemMapper;
import com.axis.goal.mapper.ChecklistMapper;
import com.axis.goal.model.dto.*;
import com.axis.goal.model.entity.Checklist;
import com.axis.goal.model.entity.ChecklistItem;
import com.axis.goal.model.enums.OwnerType;
import com.axis.goal.repository.ChecklistItemRepository;
import com.axis.goal.repository.ChecklistRepository;
import com.axis.goal.repository.GoalRepository;
import com.axis.goal.repository.SubGoalRepository;
import com.axis.goal.service.ChecklistService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class ChecklistServicePg implements ChecklistService {

    @Inject
    GoalRepository goalRepository;

    @Inject
    SubGoalRepository subGoalRepository;

    @Inject
    ChecklistRepository checklistRepository;

    @Inject
    ChecklistItemRepository checklistItemRepository;

    @Inject
    ChecklistMapper checklistMapper;

    @Inject
    ChecklistItemMapper checklistItemMapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public ChecklistResponse createChecklist(ChecklistRequest request) {
        UUID userId = getCurrentUserId();
        verifyOwnership(request.ownerId(), request.ownerType(), userId);

        Checklist checklist = checklistMapper.toEntity(request);
        checklist.setOwnerId(request.ownerId());
        checklist.setOwnerType(request.ownerType());
        checklist.setPosition((int) checklistRepository.countByOwnerId(request.ownerId()));

        checklistRepository.persist(checklist);
        log.info("Created checklist: {} for owner: {} ({}) by user: {}",
                checklist.getId(), request.ownerId(), request.ownerType(), userId);

        return checklistMapper.toResponse(checklist);
    }

    @Override
    public List<ChecklistResponse> findAllChecklists(UUID ownerId) {
        return checklistRepository.findByOwnerId(ownerId).stream()
                .map(checklistMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChecklistResponse patchChecklist(UUID checklistId, ChecklistRequest request) {
        UUID userId = getCurrentUserId();
        Checklist checklist = findAndVerifyChecklist(checklistId, userId);
        checklistMapper.patchEntity(request, checklist);

        log.info("Patched checklist: {} by user: {}", checklistId, userId);
        return checklistMapper.toResponse(checklist);
    }

    @Override
    @Transactional
    public void deleteChecklist(UUID checklistId) {
        UUID userId = getCurrentUserId();
        Checklist checklist = findAndVerifyChecklist(checklistId, userId);

        int deletedPosition = checklist.getPosition();
        UUID ownerId = checklist.getOwnerId();
        checklistRepository.delete(checklist);

        checklistRepository.findByOwnerId(ownerId).stream()
                .filter(c -> c.getPosition() > deletedPosition)
                .forEach(c -> c.setPosition(c.getPosition() - 1));

        log.info("Deleted checklist: {} by user: {}", checklistId, userId);
    }

    @Override
    @Transactional
    public ChecklistItemResponse createItem(UUID checklistId, ChecklistItemRequest request) {
        UUID userId = getCurrentUserId();
        Checklist checklist = findAndVerifyChecklist(checklistId, userId);

        ChecklistItem item = checklistItemMapper.toEntity(request);
        item.setChecklist(checklist);
        item.setPosition((int) checklistItemRepository.countByChecklistId(checklistId));
        item.setCompleted(false);

        checklistItemRepository.persist(item);
        log.info("Created item: {} in checklist: {} by user: {}", item.getId(), checklistId, userId);

        return checklistItemMapper.toResponse(item);
    }

    @Override
    @Transactional
    public ChecklistItemResponse patchItem(UUID checklistId, UUID itemId, ChecklistItemRequest request) {
        UUID userId = getCurrentUserId();
        findAndVerifyChecklist(checklistId, userId);

        ChecklistItem item = findItemInChecklist(itemId, checklistId);
        checklistItemMapper.patchEntity(request, item);

        log.info("Patched item: {} in checklist: {} by user: {}", itemId, checklistId, userId);
        return checklistItemMapper.toResponse(item);
    }

    @Override
    @Transactional
    public ChecklistItemResponse reorderItem(UUID checklistId, UUID itemId, int newPosition) {
        UUID userId = getCurrentUserId();
        findAndVerifyChecklist(checklistId, userId);

        List<ChecklistItem> items = checklistItemRepository.findByChecklistId(checklistId);
        ChecklistItem target = items.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("ChecklistItem", itemId));

        items.remove(target);
        int clampedPosition = Math.min(newPosition, items.size());
        items.add(clampedPosition, target);

        for (int i = 0; i < items.size(); i++) {
            items.get(i).setPosition(i);
        }

        log.info("Reordered item: {} to position: {} in checklist: {} by user: {}", itemId, clampedPosition, checklistId, userId);
        return checklistItemMapper.toResponse(target);
    }

    @Override
    @Transactional
    public void deleteItem(UUID checklistId, UUID itemId) {
        UUID userId = getCurrentUserId();
        findAndVerifyChecklist(checklistId, userId);

        ChecklistItem item = findItemInChecklist(itemId, checklistId);
        int deletedPosition = item.getPosition();
        checklistItemRepository.delete(item);

        checklistItemRepository.findByChecklistId(checklistId).stream()
                .filter(i -> i.getPosition() > deletedPosition)
                .forEach(i -> i.setPosition(i.getPosition() - 1));

        log.info("Deleted item: {} in checklist: {} by user: {}", itemId, checklistId, userId);
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }

    private void verifyOwnership(UUID ownerId, OwnerType ownerType, UUID userId) {
        switch (ownerType) {
            case GOAL -> goalRepository.findByIdAndUserId(ownerId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Goal", ownerId));
            case SUB_GOAL -> subGoalRepository.findByIdAndUserId(ownerId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("SubGoal", ownerId));
            default -> throw new IllegalArgumentException("Checklists not supported for owner type: " + ownerType);
        }
    }

    private Checklist findAndVerifyChecklist(UUID checklistId, UUID userId) {
        Checklist checklist = checklistRepository.findByIdOptional(checklistId)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist", checklistId));
        verifyOwnership(checklist.getOwnerId(), checklist.getOwnerType(), userId);
        return checklist;
    }

    private ChecklistItem findItemInChecklist(UUID itemId, UUID checklistId) {
        return checklistItemRepository.findByIdAndChecklistId(itemId, checklistId)
                .orElseThrow(() -> new ResourceNotFoundException("ChecklistItem", itemId));
    }
}
