package com.axis.notification.service.pg;

import com.axis.notification.mapper.NotificationLogMapper;
import com.axis.notification.model.dto.PageResponse;
import com.axis.notification.model.dto.NotificationLogRequest;
import com.axis.notification.model.dto.NotificationLogResponse;
import com.axis.notification.model.entity.NotificationLog;
import com.axis.notification.repository.NotificationLogRepository;
import com.axis.notification.service.NotificationLogService;
import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
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
public class NotificationLogServicePg implements NotificationLogService {

    @Inject
    NotificationLogRepository repository;

    @Inject
    NotificationLogMapper mapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public NotificationLogResponse create(NotificationLogRequest request) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Creating notification log for user: {} with channel: {}", currentUserId, request.channel());

        NotificationLog entity = mapper.toEntity(request);
        entity.setUserId(currentUserId);

        // Set default status if not provided
        if (entity.getStatus() == null) {
            entity.setStatus(NotificationLog.Status.SENT);
        }

        repository.persist(entity);
        log.info("Created notification log with id: {} for user: {}", entity.getId(), currentUserId);

        return mapper.toResponse(entity);
    }

    @Override
    public NotificationLogResponse findById(UUID id) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Finding notification log by id: {} for user: {}", id, currentUserId);

        NotificationLog entity = repository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        // Ensure user can only access their own notifications
        validateOwnership(entity, currentUserId);

        return mapper.toResponse(entity);
    }

    @Override
    public PageResponse<NotificationLogResponse> findByCurrentUser(int page, int size, String sortBy, String sortDirection) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Finding all notifications for user: {} with pagination: page={}, size={}", currentUserId, page, size);

        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.descending(sortBy)
                : Sort.ascending(sortBy);

        List<NotificationLog> notifications = repository.findByUserId(currentUserId, Page.of(page, size), sort);
        long totalElements = repository.countByUserId(currentUserId);

        log.debug("Found {} notifications for user: {}", totalElements, currentUserId);

        List<NotificationLogResponse> content = notifications.stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.of(content, totalElements, page, size);
    }

    @Override
    public PageResponse<NotificationLogResponse> findByCurrentUserAndStatus(NotificationLog.Status status, int page, int size, String sortBy, String sortDirection) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Finding notifications for user: {} with status: {} and pagination: page={}, size={}",
                currentUserId, status, page, size);

        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.descending(sortBy)
                : Sort.ascending(sortBy);

        List<NotificationLog> notifications = repository.findByUserIdAndStatus(currentUserId, status, Page.of(page, size), sort);
        long totalElements = repository.countByUserIdAndStatus(currentUserId, status);

        log.debug("Found {} notifications with status {} for user: {}", totalElements, status, currentUserId);

        List<NotificationLogResponse> content = notifications.stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.of(content, totalElements, page, size);
    }

    @Override
    public PageResponse<NotificationLogResponse> findByCurrentUserAndChannel(NotificationLog.Channel channel, int page, int size, String sortBy, String sortDirection) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Finding notifications for user: {} with channel: {} and pagination: page={}, size={}",
                currentUserId, channel, page, size);

        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.descending(sortBy)
                : Sort.ascending(sortBy);

        List<NotificationLog> notifications = repository.findByUserIdAndChannel(currentUserId, channel, Page.of(page, size), sort);
        long totalElements = repository.countByUserIdAndChannel(currentUserId, channel);

        log.debug("Found {} notifications with channel {} for user: {}", totalElements, channel, currentUserId);

        List<NotificationLogResponse> content = notifications.stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.of(content, totalElements, page, size);
    }

    @Override
    @Transactional
    public NotificationLogResponse updateStatus(UUID id, NotificationLog.Status status) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Updating notification {} status to {} for user: {}", id, status, currentUserId);

        NotificationLog entity = repository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        // Ensure user can only update their own notifications
        validateOwnership(entity, currentUserId);

        entity.setStatus(status);

        log.info("Updated notification {} status to {} for user: {}", id, status, currentUserId);
        return mapper.toResponse(entity);
    }

    @Override
    public long countUnread() {
        UUID currentUserId = getCurrentUserId();
        log.debug("Counting unread notifications for user: {}", currentUserId);

        long count = repository.countByUserIdAndStatus(currentUserId, NotificationLog.Status.SENT);
        log.debug("User {} has {} unread notifications", currentUserId, count);

        return count;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Deleting notification {} for user: {}", id, currentUserId);

        NotificationLog entity = repository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        // Ensure user can only delete their own notifications
        validateOwnership(entity, currentUserId);

        repository.deleteById(id);
        log.info("Deleted notification {} for user: {}", id, currentUserId);
    }

    @Override
    @Transactional
    public void deleteByCurrentUser() {
        UUID currentUserId = getCurrentUserId();
        log.debug("Deleting all notifications for user: {}", currentUserId);

        repository.deleteByUserId(currentUserId);
        log.info("Deleted all notifications for user: {}", currentUserId);
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new BusinessException("User not authenticated", Response.Status.UNAUTHORIZED));
    }

    private void validateOwnership(NotificationLog entity, UUID currentUserId) {
        if (!entity.getUserId().equals(currentUserId)) {
            throw new BusinessException("Access denied to notification", Response.Status.FORBIDDEN);
        }
    }
}
