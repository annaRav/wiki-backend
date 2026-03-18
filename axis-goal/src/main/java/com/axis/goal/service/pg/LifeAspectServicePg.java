package com.axis.goal.service.pg;

import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.LifeAspectMapper;
import com.axis.goal.model.dto.LifeAspectRequest;
import com.axis.goal.model.dto.LifeAspectResponse;
import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.model.entity.Label;
import com.axis.goal.model.entity.LifeAspect;
import com.axis.goal.repository.LifeAspectRepository;
import com.axis.goal.repository.LabelRepository;
import com.axis.goal.service.LifeAspectService;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class LifeAspectServicePg implements LifeAspectService {

    @Inject
    LifeAspectRepository lifeAspectRepository;

    @Inject
    LifeAspectMapper lifeAspectMapper;

    @Inject
    LabelRepository labelRepository;

    @Inject
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public LifeAspectResponse create(LifeAspectRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating life aspect for user: {}", userId);

        LifeAspect lifeAspect = lifeAspectMapper.toEntity(request);
        lifeAspect.setUserId(userId);

        setupLabels(lifeAspect, request.labelIds(), userId);

        lifeAspectRepository.persist(lifeAspect);
        log.info("Created life aspect with id: {} for user: {}", lifeAspect.getId(), userId);

        return lifeAspectMapper.toResponse(lifeAspect);
    }

    @Override
    @Transactional
    public LifeAspectResponse patch(UUID id, LifeAspectRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Patching life aspect: {} for user: {}", id, userId);

        LifeAspect existing = lifeAspectRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("LifeAspect", id));

        lifeAspectMapper.patchEntity(request, existing);
        setupLabels(existing, request.labelIds(), userId);

        log.info("Patched life aspect: {} for user: {}", id, userId);
        return lifeAspectMapper.toResponse(existing);
    }

    @Override
    public LifeAspectResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Finding life aspect: {} for user: {}", id, userId);

        LifeAspect lifeAspect = lifeAspectRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("LifeAspect", id));

        return lifeAspectMapper.toResponse(lifeAspect);
    }

    @Override
    public PageResponse<LifeAspectResponse> findAll(int page, int size, String sortBy, String sortDirection) {
        UUID userId = getCurrentUserId();
        log.debug("Finding all life aspects for user: {}", userId);

        Sort sort = createSort(sortBy, sortDirection);
        List<LifeAspect> aspects = lifeAspectRepository.findByUserId(userId, Page.of(page, size), sort);
        long totalElements = lifeAspectRepository.countByUserId(userId);

        List<LifeAspectResponse> responses = aspects.stream()
                .map(lifeAspectMapper::toResponse)
                .toList();

        return PageResponse.of(responses, totalElements, page, size);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Deleting life aspect: {} for user: {}", id, userId);

        if (!lifeAspectRepository.existsByIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("LifeAspect", id);
        }

        lifeAspectRepository.deleteByIdAndUserId(id, userId);
        log.info("Deleted life aspect: {} for user: {}", id, userId);
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }

    private Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "createdAt";
        }
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.Ascending
                : Sort.Direction.Descending;
        return Sort.by(sortBy, direction);
    }

    private void setupLabels(LifeAspect lifeAspect, List<UUID> labelIds, UUID userId) {
        if (labelIds == null) {
            return;
        }
        if (labelIds.isEmpty()) {
            lifeAspect.getLabels().clear();
            return;
        }
        List<Label> labels = labelRepository.findByIdsAndUserId(labelIds, userId);
        lifeAspect.getLabels().clear();
        lifeAspect.getLabels().addAll(labels);
    }
}
