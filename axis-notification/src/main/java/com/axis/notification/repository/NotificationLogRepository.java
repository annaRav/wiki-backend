package com.axis.notification.repository;

import com.axis.notification.model.entity.NotificationLog;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NotificationLogRepository implements PanacheRepositoryBase<NotificationLog, UUID> {

    /**
     * Find all notifications for a specific user with pagination
     */
    public List<NotificationLog> findByUserId(UUID userId, Page page, Sort sort) {
        return find("userId", sort, userId)
                .page(page)
                .list();
    }

    /**
     * Count notifications for a specific user
     */
    public long countByUserId(UUID userId) {
        return count("userId", userId);
    }

    /**
     * Find notifications by user ID and status with pagination
     */
    public List<NotificationLog> findByUserIdAndStatus(UUID userId, NotificationLog.Status status, Page page, Sort sort) {
        return find("userId = ?1 and status = ?2", sort, userId, status)
                .page(page)
                .list();
    }

    /**
     * Count notifications by user ID and status
     */
    public long countByUserIdAndStatus(UUID userId, NotificationLog.Status status) {
        return count("userId = ?1 and status = ?2", userId, status);
    }

    /**
     * Find notifications by user ID, channel, and status
     */
    public List<NotificationLog> findByUserIdAndChannelAndStatus(UUID userId, NotificationLog.Channel channel, NotificationLog.Status status) {
        return list("userId = ?1 and channel = ?2 and status = ?3", userId, channel, status);
    }

    /**
     * Find notifications by user ID and channel with pagination
     */
    public List<NotificationLog> findByUserIdAndChannel(UUID userId, NotificationLog.Channel channel, Page page, Sort sort) {
        return find("userId = ?1 and channel = ?2", sort, userId, channel)
                .page(page)
                .list();
    }

    /**
     * Count notifications by user ID and channel
     */
    public long countByUserIdAndChannel(UUID userId, NotificationLog.Channel channel) {
        return count("userId = ?1 and channel = ?2", userId, channel);
    }

    /**
     * Delete all notifications for a specific user
     */
    public long deleteByUserId(UUID userId) {
        return delete("userId", userId);
    }
}
