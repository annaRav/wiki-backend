package com.axis.user.service.pg;

import com.axis.common.exception.BusinessException;
import com.axis.common.security.SecurityUtils;
import com.axis.user.mapper.UserSocialLinksMapper;
import com.axis.user.model.dto.UserSocialLinksRequest;
import com.axis.user.model.dto.UserSocialLinksResponse;
import com.axis.user.model.entity.UserSocialLinks;
import com.axis.user.repository.UserSocialLinksRepository;
import com.axis.user.service.UserSocialLinksService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class UserSocialLinksServicePg implements UserSocialLinksService {

    @Inject
    UserSocialLinksRepository repository;

    @Inject
    UserSocialLinksMapper mapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    public UserSocialLinksResponse get() {
        UUID userId = getCurrentUserId();
        UserSocialLinks links = repository.findByUserId(userId)
                .orElseGet(() -> createEmpty(userId));
        return mapper.toResponse(links);
    }

    @Override
    @Transactional
    public UserSocialLinksResponse upsert(UserSocialLinksRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Upserting social links for user: {}", userId);

        UserSocialLinks links = repository.findByUserId(userId)
                .orElseGet(() -> UserSocialLinks.builder().userId(userId).build());

        mapper.updateEntity(request, links);
        repository.persist(links);

        log.info("Upserted social links for user: {}", userId);
        return mapper.toResponse(links);
    }

    @Override
    @Transactional
    public void delete() {
        UUID userId = getCurrentUserId();
        log.debug("Deleting social links for user: {}", userId);
        repository.deleteByUserId(userId);
        log.info("Deleted social links for user: {}", userId);
    }

    @Transactional
    UserSocialLinks createEmpty(UUID userId) {
        UserSocialLinks links = UserSocialLinks.builder().userId(userId).build();
        repository.persist(links);
        return links;
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new BusinessException("User not authenticated", Response.Status.UNAUTHORIZED));
    }
}