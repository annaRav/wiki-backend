package com.axis.goal.service.pg;

import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.LabelMapper;
import com.axis.goal.model.dto.LabelRequest;
import com.axis.goal.model.dto.LabelResponse;
import com.axis.goal.model.entity.Label;
import com.axis.goal.repository.LabelRepository;
import com.axis.goal.service.LabelService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class LabelServicePg implements LabelService {

    @Inject
    LabelRepository labelRepository;

    @Inject
    LabelMapper labelMapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public LabelResponse create(LabelRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating label for user: {}", userId);

        Label label = labelMapper.toEntity(request);
        label.setUserId(userId);

        labelRepository.persist(label);
        log.info("Created label: {} for user: {}", label.getId(), userId);

        return labelMapper.toResponse(label);
    }

    @Override
    @Transactional
    public LabelResponse patch(UUID id, LabelRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Patching label: {} for user: {}", id, userId);

        Label label = labelRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Label", id));

        labelMapper.patchEntity(request, label);

        log.info("Patched label: {} for user: {}", id, userId);
        return labelMapper.toResponse(label);
    }

    @Override
    public LabelResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Finding label: {} for user: {}", id, userId);

        Label label = labelRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Label", id));

        return labelMapper.toResponse(label);
    }

    @Override
    public List<LabelResponse> findAll() {
        UUID userId = getCurrentUserId();
        log.debug("Finding all labels for user: {}", userId);

        return labelRepository.findByUserId(userId).stream()
                .map(labelMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Deleting label: {} for user: {}", id, userId);

        if (!labelRepository.existsByIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("Label", id);
        }

        labelRepository.deleteByIdAndUserId(id, userId);
        log.info("Deleted label: {} for user: {}", id, userId);
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }
}