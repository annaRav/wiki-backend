package com.axis.user.repository;

import com.axis.user.model.entity.UserSocialLinks;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserSocialLinksRepository implements PanacheRepositoryBase<UserSocialLinks, UUID> {

    public Optional<UserSocialLinks> findByUserId(UUID userId) {
        return find("userId", userId).firstResultOptional();
    }

    public void deleteByUserId(UUID userId) {
        delete("userId", userId);
    }
}