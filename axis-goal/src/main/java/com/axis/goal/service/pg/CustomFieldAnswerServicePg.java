package com.axis.goal.service.pg;

import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.CustomFieldAnswerMapper;
import com.axis.goal.model.dto.CustomFieldAnswerRequest;
import com.axis.goal.model.dto.CustomFieldAnswerResponse;
import com.axis.goal.model.entity.CustomFieldAnswer;
import com.axis.goal.model.entity.CustomFieldDefinition;
import com.axis.goal.model.entity.Goal;
import com.axis.goal.repository.CustomFieldAnswerRepository;
import com.axis.goal.repository.CustomFieldDefinitionRepository;
import com.axis.goal.repository.GoalRepository;
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
public class CustomFieldAnswerServicePg implements com.axis.goal.service.CustomFieldAnswerService {

    private final CustomFieldAnswerRepository answerRepository;
    private final CustomFieldDefinitionRepository definitionRepository;
    private final GoalRepository goalRepository;
    private final CustomFieldAnswerMapper answerMapper;

    @Override
    @Transactional
    public CustomFieldAnswerResponse create(UUID goalId, CustomFieldAnswerRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating custom field answer for goal: {} by user: {}", goalId, userId);

        // Verify goal exists and belongs to user
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", goalId));

        // Verify field definition exists
        CustomFieldDefinition definition = definitionRepository.findById(request.fieldDefinitionId())
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", request.fieldDefinitionId()));

        // Verify the field definition belongs to the goal's type
        if (!definition.getGoalType().getId().equals(goal.getType().getId())) {
            throw new BusinessException(
                    "Custom field does not belong to this goal's type",
                    HttpStatus.BAD_REQUEST
            );
        }

        // Check if answer already exists for this field and goal
        if (answerRepository.existsByGoalIdAndFieldDefinitionId(goalId, request.fieldDefinitionId())) {
            throw new BusinessException(
                    "Answer for this custom field already exists. Use update endpoint instead.",
                    HttpStatus.CONFLICT
            );
        }

        // Validate required fields
        if (definition.isRequired() && (request.value() == null || request.value().isBlank())) {
            throw new BusinessException(
                    "Value is required for field: " + definition.getLabel(),
                    HttpStatus.BAD_REQUEST
            );
        }

        CustomFieldAnswer answer = answerMapper.toEntity(request);
        answer.setGoal(goal);
        answer.setFieldDefinition(definition);

        CustomFieldAnswer saved = answerRepository.save(answer);
        log.info("Created custom field answer with id: {} for goal: {}", saved.getId(), goalId);

        return answerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CustomFieldAnswerResponse update(UUID id, CustomFieldAnswerRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Updating custom field answer: {} by user: {}", id, userId);

        CustomFieldAnswer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldAnswer", id));

        // Verify goal belongs to user
        if (!answer.getGoal().getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to modify this answer", HttpStatus.FORBIDDEN);
        }

        // Validate required fields
        if (answer.getFieldDefinition().isRequired() &&
                (request.value() == null || request.value().isBlank())) {
            throw new BusinessException(
                    "Value is required for field: " + answer.getFieldDefinition().getLabel(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Update only the value (field definition should not change)
        answer.setValue(request.value());

        log.info("Updated custom field answer: {}", id);
        return answerMapper.toResponse(answer);
    }

    @Override
    public CustomFieldAnswerResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Finding custom field answer: {} by user: {}", id, userId);

        CustomFieldAnswer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldAnswer", id));

        // Verify goal belongs to user
        if (!answer.getGoal().getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to view this answer", HttpStatus.FORBIDDEN);
        }

        return answerMapper.toResponse(answer);
    }

    @Override
    public List<CustomFieldAnswerResponse> findByGoalId(UUID goalId) {
        UUID userId = getCurrentUserId();
        log.debug("Finding custom field answers for goal: {} by user: {}", goalId, userId);

        // Verify goal exists and belongs to user
        Goal goal = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", goalId));

        return answerRepository.findByGoalId(goalId)
                .stream()
                .map(answerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Deleting custom field answer: {} by user: {}", id, userId);

        CustomFieldAnswer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldAnswer", id));

        // Verify goal belongs to user
        if (!answer.getGoal().getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to delete this answer", HttpStatus.FORBIDDEN);
        }

        // Check if field is required
        if (answer.getFieldDefinition().isRequired()) {
            throw new BusinessException(
                    "Cannot delete answer for required field: " + answer.getFieldDefinition().getLabel(),
                    HttpStatus.BAD_REQUEST
            );
        }

        answerRepository.delete(answer);
        log.info("Deleted custom field answer: {}", id);
    }

    private UUID getCurrentUserId() {
        return SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }
}