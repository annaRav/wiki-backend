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
import com.axis.goal.service.CustomFieldDefinitionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class CustomFieldDefinitionServicePg implements CustomFieldDefinitionService {

    @Inject
    CustomFieldDefinitionRepository definitionRepository;

    @Inject
    GoalTypeRepository goalTypeRepository;

    @Inject
    CustomFieldDefinitionMapper definitionMapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public CustomFieldDefinitionResponse create(UUID goalTypeId, CustomFieldDefinitionRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating custom field definition for goal type: {} by user: {}", goalTypeId, userId);

        // Verify goal type exists and belongs to user
        GoalType goalType = goalTypeRepository.findByIdAndUserId(goalTypeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("GoalType", goalTypeId));

        CustomFieldDefinition definition = definitionMapper.toEntity(request);
        definition.setGoalType(goalType);

        definitionRepository.persist(definition);
        log.info("Created custom field definition with id: {} for goal type: {}", definition.getId(), goalTypeId);

        return definitionMapper.toResponse(definition);
    }

    @Override
    @Transactional
    public CustomFieldDefinitionResponse update(UUID id, CustomFieldDefinitionRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Updating custom field definition: {} by user: {}", id, userId);

        CustomFieldDefinition definition = definitionRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", id));

        // Verify goal type belongs to user
        if (!definition.getGoalType().getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to modify this custom field", Response.Status.FORBIDDEN);
        }

        definitionMapper.updateEntity(request, definition);

        log.info("Updated custom field definition: {}", id);
        return definitionMapper.toResponse(definition);
    }

    @Override
    public CustomFieldDefinitionResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Finding custom field definition: {} by user: {}", id, userId);

        CustomFieldDefinition definition = definitionRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", id));

        // Verify goal type belongs to user
        if (!definition.getGoalType().getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to view this custom field", Response.Status.FORBIDDEN);
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

        CustomFieldDefinition definition = definitionRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", id));

        // Verify goal type belongs to user
        if (!definition.getGoalType().getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to delete this custom field", Response.Status.FORBIDDEN);
        }

        definitionRepository.delete(definition);
        log.info("Deleted custom field definition: {}", id);
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }
}
