package com.axis.user.repository;

import com.axis.user.model.entity.UserSettings;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserSettingsRepository implements PanacheRepositoryBase<UserSettings, UUID> {

    public Optional<UserSettings> findByUserId(UUID userId) {
        return find("userId", userId).firstResultOptional();
    }

    public void deleteByUserId(UUID userId) {
        delete("userId", userId);
    }
}