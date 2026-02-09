package com.axis.notification.service.pg;

import com.axis.notification.mapper.NotificationTemplatesMapper;
import com.axis.notification.model.dto.PageResponse;
import com.axis.notification.model.dto.NotificationTemplateRequest;
import com.axis.notification.model.dto.NotificationTemplateResponse;
import com.axis.notification.model.entity.NotificationTemplates;
import com.axis.notification.repository.NotificationTemplatesRepository;
import com.axis.notification.service.NotificationTemplatesService;
import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class NotificationTemplatesServicePg implements NotificationTemplatesService {

    @Inject
    NotificationTemplatesRepository repository;

    @Inject
    NotificationTemplatesMapper mapper;

    @Override
    @Transactional
    public NotificationTemplateResponse create(NotificationTemplateRequest request) {
        log.debug("Creating notification template with type: {}", request.type());

        // Check if template with this type already exists
        if (repository.existsByType(request.type())) {
            throw new BusinessException(
                    "Notification template with type " + request.type() + " already exists",
                    Response.Status.CONFLICT
            );
        }

        NotificationTemplates entity = mapper.toEntity(request);
        repository.persist(entity);

        log.info("Created notification template with id: {} and type: {}", entity.getId(), entity.getType());
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public NotificationTemplateResponse update(UUID id, NotificationTemplateRequest request) {
        log.debug("Updating notification template with id: {}", id);

        NotificationTemplates entity = repository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found with id: " + id));

        // Check if another template with the same type exists (excluding current one)
        repository.findByType(request.type()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BusinessException(
                        "Another notification template with type " + request.type() + " already exists",
                        Response.Status.CONFLICT
                );
            }
        });

        mapper.updateEntity(request, entity);

        log.info("Updated notification template with id: {}", id);
        return mapper.toResponse(entity);
    }

    @Override
    public NotificationTemplateResponse findById(UUID id) {
        log.debug("Finding notification template by id: {}", id);

        NotificationTemplates entity = repository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found with id: " + id));

        return mapper.toResponse(entity);
    }

    @Override
    public NotificationTemplateResponse findByType(NotificationTemplates.Type type) {
        log.debug("Finding notification template by type: {}", type);

        NotificationTemplates entity = repository.findByType(type)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found with type: " + type));

        return mapper.toResponse(entity);
    }

    @Override
    public PageResponse<NotificationTemplateResponse> findAll(int page, int size, String sortBy, String sortDirection) {
        log.debug("Finding all notification templates with pagination: page={}, size={}", page, size);

        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.descending(sortBy)
                : Sort.ascending(sortBy);

        List<NotificationTemplates> templates = repository.findAll(Page.of(page, size), sort);
        long totalElements = repository.countAll();

        log.debug("Found {} notification templates", totalElements);

        List<NotificationTemplateResponse> content = templates.stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.of(content, totalElements, page, size);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.debug("Deleting notification template with id: {}", id);

        if (!repository.findByIdOptional(id).isPresent()) {
            throw new ResourceNotFoundException("Notification template not found with id: " + id);
        }

        repository.deleteById(id);
        log.info("Deleted notification template with id: {}", id);
    }
}
