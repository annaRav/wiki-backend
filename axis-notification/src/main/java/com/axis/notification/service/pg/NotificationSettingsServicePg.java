package com.axis.notification.service.pg;

import com.axis.notification.mapper.NotificationSettingsMapper;
import com.axis.notification.model.dto.NotificationSettingsRequest;
import com.axis.notification.model.dto.NotificationSettingsResponse;
import com.axis.notification.model.entity.NotificationSettings;
import com.axis.notification.repository.NotificationSettingsRepository;
import com.axis.notification.service.NotificationSettingsService;
import com.axis.common.exception.BusinessException;
import com.axis.common.security.SecurityUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class NotificationSettingsServicePg implements NotificationSettingsService {

    @Inject
    NotificationSettingsRepository repository;

    @Inject
    NotificationSettingsMapper mapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public NotificationSettingsResponse createOrUpdate(NotificationSettingsRequest request) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Creating or updating notification settings for user: {}", currentUserId);

        NotificationSettings entity = repository.findByUserId(currentUserId)
                .map(existing -> {
                    log.debug("Updating existing notification settings for user: {}", currentUserId);
                    mapper.updateEntity(request, existing);
                    return existing;
                })
                .orElseGet(() -> {
                    log.debug("Creating new notification settings for user: {}", currentUserId);
                    NotificationSettings newEntity = mapper.toEntity(request);
                    newEntity.setUserId(currentUserId);
                    repository.persist(newEntity);
                    return newEntity;
                });

        log.info("Saved notification settings for user: {}", currentUserId);

        return mapper.toResponse(entity);
    }

    @Override
    public NotificationSettingsResponse getOrCreateForCurrentUser() {
        UUID currentUserId = getCurrentUserId();
        log.debug("Finding notification settings for user: {}", currentUserId);

        NotificationSettings entity = repository.findByUserId(currentUserId)
                .orElseGet(() -> {
                    log.debug("No settings found for user: {}, returning defaults", currentUserId);
                    return createDefaultSettings(currentUserId);
                });

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void deleteForCurrentUser() {
        UUID currentUserId = getCurrentUserId();
        log.debug("Deleting notification settings for user: {}", currentUserId);

        repository.deleteByUserId(currentUserId);
        log.info("Deleted notification settings for user: {}", currentUserId);
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new BusinessException("User not authenticated", Response.Status.UNAUTHORIZED));
    }

    private NotificationSettings createDefaultSettings(UUID userId) {
        log.debug("Creating default notification settings for user: {}", userId);
        return NotificationSettings.builder()
                .userId(userId)
                .enableEmail(true)
                .enablePush(true)
                .enableTelegram(false)
                .build();
    }
}
