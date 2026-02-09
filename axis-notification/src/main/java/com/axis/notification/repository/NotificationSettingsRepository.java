package com.axis.notification.repository;

import com.axis.notification.model.entity.NotificationSettings;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class NotificationSettingsRepository implements PanacheRepositoryBase<NotificationSettings, UUID> {

    /**
     * Find notification settings by user ID
     */
    public Optional<NotificationSettings> findByUserId(UUID userId) {
        return find("userId", userId).firstResultOptional();
    }

    /**
     * Check if notification settings exist for a user
     */
    public boolean existsByUserId(UUID userId) {
        return count("userId", userId) > 0;
    }

    /**
     * Delete notification settings for a specific user
     */
    public long deleteByUserId(UUID userId) {
        return delete("userId", userId);
    }
}
