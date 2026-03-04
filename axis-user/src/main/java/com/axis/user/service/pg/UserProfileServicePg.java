package com.axis.user.service.pg;

import com.axis.common.exception.BusinessException;
import com.axis.common.security.SecurityUtils;
import com.axis.user.mapper.UserProfileMapper;
import com.axis.user.model.dto.UserProfileRequest;
import com.axis.user.model.dto.UserProfileResponse;
import com.axis.user.model.entity.UserProfile;
import com.axis.user.repository.UserProfileRepository;
import com.axis.user.service.UserProfileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class UserProfileServicePg implements UserProfileService {

    @Inject
    UserProfileRepository repository;

    @Inject
    UserProfileMapper mapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    public UserProfileResponse get() {
        UUID userId = getCurrentUserId();
        UserProfile profile = repository.findByUserId(userId)
                .orElseGet(() -> createDefault(userId));
        return mapper.toResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse upsert(UserProfileRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Upserting profile for user: {}", userId);

        UserProfile profile = repository.findByUserId(userId)
                .orElseGet(() -> UserProfile.builder().userId(userId).build());

        mapper.updateEntity(request, profile);
        repository.persist(profile);

        log.info("Upserted profile for user: {}", userId);
        return mapper.toResponse(profile);
    }

    @Override
    @Transactional
    public void delete() {
        UUID userId = getCurrentUserId();
        log.debug("Deleting profile for user: {}", userId);
        repository.deleteByUserId(userId);
        log.info("Deleted profile for user: {}", userId);
    }

    @Transactional
    UserProfile createDefault(UUID userId) {
        UserProfile profile = UserProfile.builder().userId(userId).build();
        repository.persist(profile);
        return profile;
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new BusinessException("User not authenticated", Response.Status.UNAUTHORIZED));
    }
}