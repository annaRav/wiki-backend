package com.axis.goal.service.pg;

import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.CustomFieldAnswerMapper;
import com.axis.goal.model.dto.CustomFieldAnswerRequest;
import com.axis.goal.model.dto.CustomFieldAnswerResponse;
import com.axis.goal.model.entity.CustomFieldAnswer;
import com.axis.goal.model.entity.CustomFieldDefinition;
import com.axis.goal.repository.CustomFieldAnswerRepository;
import com.axis.goal.repository.CustomFieldDefinitionRepository;
import com.axis.goal.service.CustomFieldAnswerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class CustomFieldAnswerServicePg implements CustomFieldAnswerService {

    @Inject
    CustomFieldAnswerRepository answerRepository;

    @Inject
    CustomFieldDefinitionRepository definitionRepository;

    @Inject
    CustomFieldAnswerMapper answerMapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public CustomFieldAnswerResponse create(CustomFieldAnswerRequest request) {
        UUID userId = getCurrentUserId();
        UUID ownerId = request.ownerId();
        log.debug("Creating custom field answer for owner: {} by user: {}", ownerId, userId);

        CustomFieldDefinition definition = definitionRepository.findByIdOptional(request.fieldDefinitionId())
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", request.fieldDefinitionId()));

        if (!definition.getUserId().equals(userId)) {
            throw new BusinessException("Custom field does not belong to your account", Response.Status.FORBIDDEN);
        }

        if (answerRepository.existsByOwnerIdAndFieldDefinitionId(ownerId, request.fieldDefinitionId())) {
            throw new BusinessException(
                    "Answer for this custom field already exists. Use update endpoint instead.",
                    Response.Status.CONFLICT
            );
        }

        if (definition.isRequired() && (request.value() == null || request.value().isBlank())) {
            throw new BusinessException(
                    "Value is required for field: " + definition.getLabel(),
                    Response.Status.BAD_REQUEST
            );
        }

        CustomFieldAnswer answer = answerMapper.toEntity(request);
        answer.setOwnerId(ownerId);
        answer.setFieldDefinition(definition);

        answerRepository.persist(answer);
        log.info("Created custom field answer with id: {} for owner: {}", answer.getId(), ownerId);

        return answerMapper.toResponse(answer);
    }

    @Override
    @Transactional
    public CustomFieldAnswerResponse update(UUID id, CustomFieldAnswerRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Updating custom field answer: {} by user: {}", id, userId);

        CustomFieldAnswer answer = findOwnedAnswer(id, userId);

        if (answer.getFieldDefinition().isRequired() &&
                (request.value() == null || request.value().isBlank())) {
            throw new BusinessException(
                    "Value is required for field: " + answer.getFieldDefinition().getLabel(),
                    Response.Status.BAD_REQUEST
            );
        }

        answer.setValue(request.value());

        log.info("Updated custom field answer: {}", id);
        return answerMapper.toResponse(answer);
    }

    @Override
    @Transactional
    public CustomFieldAnswerResponse patch(UUID id, CustomFieldAnswerRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Patching custom field answer: {} by user: {}", id, userId);

        CustomFieldAnswer answer = findOwnedAnswer(id, userId);

        if (request.value() != null && answer.getFieldDefinition().isRequired() && request.value().isBlank()) {
            throw new BusinessException(
                    "Value cannot be empty for required field: " + answer.getFieldDefinition().getLabel(),
                    Response.Status.BAD_REQUEST
            );
        }

        answerMapper.patchEntity(request, answer);

        log.info("Patched custom field answer: {}", id);
        return answerMapper.toResponse(answer);
    }

    @Override
    public CustomFieldAnswerResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Finding custom field answer: {} by user: {}", id, userId);

        CustomFieldAnswer answer = findOwnedAnswer(id, userId);
        return answerMapper.toResponse(answer);
    }

    @Override
    public List<CustomFieldAnswerResponse> findByOwnerId(UUID ownerId) {
        UUID userId = getCurrentUserId();
        log.debug("Finding custom field answers for owner: {} by user: {}", ownerId, userId);

        return answerRepository.findByOwnerId(ownerId)
                .stream()
                .map(answerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Deleting custom field answer: {} by user: {}", id, userId);

        CustomFieldAnswer answer = findOwnedAnswer(id, userId);

        if (answer.getFieldDefinition().isRequired()) {
            throw new BusinessException(
                    "Cannot delete answer for required field: " + answer.getFieldDefinition().getLabel(),
                    Response.Status.BAD_REQUEST
            );
        }

        answerRepository.delete(answer);
        log.info("Deleted custom field answer: {}", id);
    }

    private CustomFieldAnswer findOwnedAnswer(UUID id, UUID userId) {
        CustomFieldAnswer answer = answerRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldAnswer", id));

        if (!answer.getFieldDefinition().getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to access this answer", Response.Status.FORBIDDEN);
        }

        return answer;
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }
}
