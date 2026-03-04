package com.axis.user.service.pg;

import com.axis.common.exception.BusinessException;
import com.axis.common.security.SecurityUtils;
import com.axis.user.mapper.UserSettingsMapper;
import com.axis.user.model.dto.UserSettingsRequest;
import com.axis.user.model.dto.UserSettingsResponse;
import com.axis.user.model.entity.UserSettings;
import com.axis.user.repository.UserSettingsRepository;
import com.axis.user.service.UserSettingsService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class UserSettingsServicePg implements UserSettingsService {

    @Inject
    UserSettingsRepository repository;

    @Inject
    UserSettingsMapper mapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    public UserSettingsResponse get() {
        UUID userId = getCurrentUserId();
        UserSettings settings = repository.findByUserId(userId)
                .orElseGet(() -> createDefaults(userId));
        return mapper.toResponse(settings);
    }

    @Override
    @Transactional
    public UserSettingsResponse upsert(UserSettingsRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Upserting settings for user: {}", userId);

        UserSettings settings = repository.findByUserId(userId)
                .orElseGet(() -> UserSettings.builder().userId(userId).build());

        mapper.updateEntity(request, settings);
        repository.persist(settings);

        log.info("Upserted settings for user: {}", userId);
        return mapper.toResponse(settings);
    }

    @Override
    @Transactional
    public void delete() {
        UUID userId = getCurrentUserId();
        log.debug("Deleting settings for user: {}", userId);
        repository.deleteByUserId(userId);
        log.info("Deleted settings for user: {}", userId);
    }

    @Transactional
    UserSettings createDefaults(UUID userId) {
        UserSettings settings = UserSettings.builder().userId(userId).build();
        repository.persist(settings);
        return settings;
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new BusinessException("User not authenticated", Response.Status.UNAUTHORIZED));
    }
}