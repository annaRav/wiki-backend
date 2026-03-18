package com.axis.goal.service.pg;

import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.CustomFieldDefinitionMapper;
import com.axis.goal.model.dto.CustomFieldDefinitionRequest;
import com.axis.goal.model.dto.CustomFieldDefinitionResponse;
import com.axis.goal.model.entity.CustomFieldDefinition;
import com.axis.goal.model.enums.OwnerType;
import com.axis.goal.repository.CustomFieldDefinitionRepository;
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
    CustomFieldDefinitionMapper definitionMapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public CustomFieldDefinitionResponse create(CustomFieldDefinitionRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating custom field definition for owner type: {} by user: {}", request.ownerType(), userId);

        CustomFieldDefinition definition = definitionMapper.toEntity(request);
        definition.setUserId(userId);

        definitionRepository.persist(definition);
        log.info("Created custom field definition with id: {} for owner type: {}", definition.getId(), request.ownerType());

        return definitionMapper.toResponse(definition);
    }

    @Override
    @Transactional
    public CustomFieldDefinitionResponse update(UUID id, CustomFieldDefinitionRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Updating custom field definition: {} by user: {}", id, userId);

        CustomFieldDefinition definition = findOwnedDefinition(id, userId);
        definitionMapper.updateEntity(request, definition);

        log.info("Updated custom field definition: {}", id);
        return definitionMapper.toResponse(definition);
    }

    @Override
    @Transactional
    public CustomFieldDefinitionResponse patch(UUID id, CustomFieldDefinitionRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Patching custom field definition: {} by user: {}", id, userId);

        CustomFieldDefinition definition = findOwnedDefinition(id, userId);
        definitionMapper.patchEntity(request, definition);

        log.info("Patched custom field definition: {}", id);
        return definitionMapper.toResponse(definition);
    }

    @Override
    public CustomFieldDefinitionResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Finding custom field definition: {} by user: {}", id, userId);

        CustomFieldDefinition definition = findOwnedDefinition(id, userId);
        return definitionMapper.toResponse(definition);
    }

    @Override
    public List<CustomFieldDefinitionResponse> findByOwnerType(OwnerType ownerType) {
        UUID userId = getCurrentUserId();
        log.debug("Finding custom field definitions for owner type: {} by user: {}", ownerType, userId);

        return definitionRepository.findByOwnerTypeAndUserId(ownerType, userId)
                .stream()
                .map(definitionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Deleting custom field definition: {} by user: {}", id, userId);

        CustomFieldDefinition definition = findOwnedDefinition(id, userId);
        definitionRepository.delete(definition);
        log.info("Deleted custom field definition: {}", id);
    }

    private CustomFieldDefinition findOwnedDefinition(UUID id, UUID userId) {
        CustomFieldDefinition definition = definitionRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", id));

        if (!definition.getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to access this custom field", Response.Status.FORBIDDEN);
        }

        return definition;
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }
}
