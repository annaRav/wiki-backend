package com.axis.goal.service.pg;

import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.CustomFieldDefinitionMapper;
import com.axis.goal.model.dto.CustomFieldDefinitionRequest;
import com.axis.goal.model.dto.CustomFieldDefinitionResponse;
import com.axis.goal.model.entity.CustomFieldDefinition;
import com.axis.goal.model.entity.GoalType;
import com.axis.goal.repository.CustomFieldDefinitionRepository;
import com.axis.goal.repository.GoalTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomFieldDefinitionServicePg implements com.axis.goal.service.CustomFieldDefinitionService {

    private final CustomFieldDefinitionRepository definitionRepository;
    private final GoalTypeRepository goalTypeRepository;
    private final CustomFieldDefinitionMapper definitionMapper;

    @Override
    @Transactional
    public CustomFieldDefinitionResponse create(UUID goalTypeId, CustomFieldDefinitionRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating custom field definition for goal type: {} by user: {}", goalTypeId, userId);

        // Verify goal type exists and belongs to user
        GoalType goalType = goalTypeRepository.findByIdAndUserId(goalTypeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("GoalType", goalTypeId));

        // Check if key already exists for this goal type
        if (definitionRepository.existsByGoalTypeIdAndKey(goalTypeId, request.key())) {
            throw new BusinessException(
                    "Custom field with key '" + request.key() + "' already exists for this goal type",
                    HttpStatus.CONFLICT
            );
        }

        CustomFieldDefinition definition = definitionMapper.toEntity(request);
        definition.setGoalType(goalType);

        CustomFieldDefinition saved = definitionRepository.save(definition);
        log.info("Created custom field definition with id: {} for goal type: {}", saved.getId(), goalTypeId);

        return definitionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CustomFieldDefinitionResponse update(UUID id, CustomFieldDefinitionRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Updating custom field definition: {} by user: {}", id, userId);

        CustomFieldDefinition definition = definitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", id));

        // Verify goal type belongs to user
        if (!definition.getGoalType().getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to modify this custom field", HttpStatus.FORBIDDEN);
        }

        // Check if new key conflicts with existing keys (excluding current definition)
        if (!definition.getKey().equals(request.key()) &&
                definitionRepository.existsByGoalTypeIdAndKey(definition.getGoalType().getId(), request.key())) {
            throw new BusinessException(
                    "Custom field with key '" + request.key() + "' already exists for this goal type",
                    HttpStatus.CONFLICT
            );
        }

        definitionMapper.updateEntity(request, definition);

        log.info("Updated custom field definition: {}", id);
        return definitionMapper.toResponse(definition);
    }

    @Override
    public CustomFieldDefinitionResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Finding custom field definition: {} by user: {}", id, userId);

        CustomFieldDefinition definition = definitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", id));

        // Verify goal type belongs to user
        if (!definition.getGoalType().getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to view this custom field", HttpStatus.FORBIDDEN);
        }

        return definitionMapper.toResponse(definition);
    }

    @Override
    public List<CustomFieldDefinitionResponse> findByGoalTypeId(UUID goalTypeId) {
        UUID userId = getCurrentUserId();
        log.debug("Finding custom field definitions for goal type: {} by user: {}", goalTypeId, userId);

        // Verify goal type exists and belongs to user
        GoalType goalType = goalTypeRepository.findByIdAndUserId(goalTypeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("GoalType", goalTypeId));

        return definitionRepository.findByGoalTypeId(goalTypeId)
                .stream()
                .map(definitionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Deleting custom field definition: {} by user: {}", id, userId);

        CustomFieldDefinition definition = definitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", id));

        // Verify goal type belongs to user
        if (!definition.getGoalType().getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to delete this custom field", HttpStatus.FORBIDDEN);
        }

        definitionRepository.delete(definition);
        log.info("Deleted custom field definition: {}", id);
    }

    @Override
    public boolean existsByGoalTypeIdAndKey(UUID goalTypeId, String key) {
        return definitionRepository.existsByGoalTypeIdAndKey(goalTypeId, key);
    }

    private UUID getCurrentUserId() {
        return SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }
}