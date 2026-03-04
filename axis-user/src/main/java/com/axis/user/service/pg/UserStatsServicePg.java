package com.axis.user.service.pg;

import com.axis.common.exception.BusinessException;
import com.axis.common.security.SecurityUtils;
import com.axis.user.mapper.UserStatsMapper;
import com.axis.user.model.dto.UserStatsResponse;
import com.axis.user.model.entity.UserStats;
import com.axis.user.repository.UserStatsRepository;
import com.axis.user.service.UserStatsService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class UserStatsServicePg implements UserStatsService {

    @Inject
    UserStatsRepository repository;

    @Inject
    UserStatsMapper mapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    public UserStatsResponse get() {
        UUID userId = getCurrentUserId();
        UserStats stats = repository.findByUserId(userId)
                .orElseGet(() -> createEmpty(userId));
        return mapper.toResponse(stats);
    }

    @Transactional
    UserStats createEmpty(UUID userId) {
        UserStats stats = UserStats.builder().userId(userId).build();
        repository.persist(stats);
        return stats;
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new BusinessException("User not authenticated", Response.Status.UNAUTHORIZED));
    }
}