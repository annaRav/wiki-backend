package com.axis.user.repository;

import com.axis.user.model.entity.UserProfile;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserProfileRepository implements PanacheRepositoryBase<UserProfile, UUID> {

    public Optional<UserProfile> findByUserId(UUID userId) {
        return find("userId", userId).firstResultOptional();
    }

    public void deleteByUserId(UUID userId) {
        delete("userId", userId);
    }
}