package com.axis.goal.service.pg;

import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.ChecklistItemMapper;
import com.axis.goal.mapper.ChecklistMapper;
import com.axis.goal.model.dto.*;
import com.axis.goal.model.entity.Checklist;
import com.axis.goal.model.entity.ChecklistItem;
import com.axis.goal.model.entity.Goal;
import com.axis.goal.repository.ChecklistItemRepository;
import com.axis.goal.repository.ChecklistRepository;
import com.axis.goal.repository.GoalRepository;
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
    public ChecklistResponse createChecklist(UUID goalId, ChecklistRequest request) {
        UUID userId = getCurrentUserId();
        Goal goal = findGoalForUser(goalId, userId);

        Checklist checklist = checklistMapper.toEntity(request);
        checklist.setGoal(goal);
        checklist.setPosition((int) checklistRepository.countByGoalId(goalId));

        checklistRepository.persist(checklist);
        log.info("Created checklist: {} in goal: {} for user: {}", checklist.getId(), goalId, userId);

        return checklistMapper.toResponse(checklist);
    }

    @Override
    @Transactional
    public List<ChecklistResponse> findAllChecklists(UUID goalId) {
        UUID userId = getCurrentUserId();
        findGoalForUser(goalId, userId);

        return checklistRepository.findByGoalId(goalId).stream()
                .map(checklistMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChecklistResponse patchChecklist(UUID goalId, UUID checklistId, ChecklistRequest request) {
        UUID userId = getCurrentUserId();
        findGoalForUser(goalId, userId);

        Checklist checklist = findChecklistInGoal(checklistId, goalId);
        checklistMapper.patchEntity(request, checklist);

        log.info("Patched checklist: {} in goal: {} for user: {}", checklistId, goalId, userId);
        return checklistMapper.toResponse(checklist);
    }

    @Override
    @Transactional
    public void deleteChecklist(UUID goalId, UUID checklistId) {
        UUID userId = getCurrentUserId();
        findGoalForUser(goalId, userId);

        Checklist checklist = findChecklistInGoal(checklistId, goalId);
        int deletedPosition = checklist.getPosition();
        checklistRepository.delete(checklist);

        // Shift positions of remaining checklists to fill the gap
        checklistRepository.findByGoalId(goalId).stream()
                .filter(c -> c.getPosition() > deletedPosition)
                .forEach(c -> c.setPosition(c.getPosition() - 1));

        log.info("Deleted checklist: {} in goal: {} for user: {}", checklistId, goalId, userId);
    }

    @Override
    @Transactional
    public ChecklistItemResponse createItem(UUID goalId, UUID checklistId, ChecklistItemRequest request) {
        UUID userId = getCurrentUserId();
        findGoalForUser(goalId, userId);

        Checklist checklist = findChecklistInGoal(checklistId, goalId);

        ChecklistItem item = checklistItemMapper.toEntity(request);
        item.setChecklist(checklist);
        item.setPosition((int) checklistItemRepository.countByChecklistId(checklistId));
        item.setCompleted(false);

        checklistItemRepository.persist(item);
        log.info("Created item: {} in checklist: {} for user: {}", item.getId(), checklistId, userId);

        return checklistItemMapper.toResponse(item);
    }

    @Override
    @Transactional
    public ChecklistItemResponse patchItem(UUID goalId, UUID checklistId, UUID itemId, ChecklistItemRequest request) {
        UUID userId = getCurrentUserId();
        findGoalForUser(goalId, userId);
        findChecklistInGoal(checklistId, goalId);

        ChecklistItem item = findItemInChecklist(itemId, checklistId);
        checklistItemMapper.patchEntity(request, item);

        log.info("Patched item: {} in checklist: {} for user: {}", itemId, checklistId, userId);
        return checklistItemMapper.toResponse(item);
    }

    @Override
    @Transactional
    public ChecklistItemResponse reorderItem(UUID goalId, UUID checklistId, UUID itemId, int newPosition) {
        UUID userId = getCurrentUserId();
        findGoalForUser(goalId, userId);
        findChecklistInGoal(checklistId, goalId);

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

        log.info("Reordered item: {} to position: {} in checklist: {} for user: {}", itemId, clampedPosition, checklistId, userId);
        return checklistItemMapper.toResponse(target);
    }

    @Override
    @Transactional
    public void deleteItem(UUID goalId, UUID checklistId, UUID itemId) {
        UUID userId = getCurrentUserId();
        findGoalForUser(goalId, userId);
        findChecklistInGoal(checklistId, goalId);

        ChecklistItem item = findItemInChecklist(itemId, checklistId);
        int deletedPosition = item.getPosition();
        checklistItemRepository.delete(item);

        // Shift positions of remaining items to fill the gap
        checklistItemRepository.findByChecklistId(checklistId).stream()
                .filter(i -> i.getPosition() > deletedPosition)
                .forEach(i -> i.setPosition(i.getPosition() - 1));

        log.info("Deleted item: {} in checklist: {} for user: {}", itemId, checklistId, userId);
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }

    private Goal findGoalForUser(UUID goalId, UUID userId) {
        return goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", goalId));
    }

    private Checklist findChecklistInGoal(UUID checklistId, UUID goalId) {
        return checklistRepository.findByIdAndGoalId(checklistId, goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist", checklistId));
    }

    private ChecklistItem findItemInChecklist(UUID itemId, UUID checklistId) {
        return checklistItemRepository.findByIdAndChecklistId(itemId, checklistId)
                .orElseThrow(() -> new ResourceNotFoundException("ChecklistItem", itemId));
    }
}
