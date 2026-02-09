package com.axis.notification.service;

import com.axis.notification.model.dto.PageResponse;
import com.axis.notification.model.dto.NotificationLogRequest;
import com.axis.notification.model.dto.NotificationLogResponse;
import com.axis.notification.model.entity.NotificationLog;

import java.util.UUID;

public interface NotificationLogService {

    /**
     * Create a new notification log entry for the current user
     */
    NotificationLogResponse create(NotificationLogRequest request);

    /**
     * Find notification by ID (must belong to current user)
     */
    NotificationLogResponse findById(UUID id);

    /**
     * Find all notifications for the current user
     */
    PageResponse<NotificationLogResponse> findByCurrentUser(int page, int size, String sortBy, String sortDirection);

    /**
     * Find notifications by current user and status
     */
    PageResponse<NotificationLogResponse> findByCurrentUserAndStatus(NotificationLog.Status status, int page, int size, String sortBy, String sortDirection);

    /**
     * Find notifications by current user and channel
     */
    PageResponse<NotificationLogResponse> findByCurrentUserAndChannel(NotificationLog.Channel channel, int page, int size, String sortBy, String sortDirection);

    /**
     * Update notification status (e.g., mark as read)
     */
    NotificationLogResponse updateStatus(UUID id, NotificationLog.Status status);

    /**
     * Delete notification by ID (must belong to current user)
     */
    void deleteById(UUID id);

    /**
     * Delete all notifications for the current user
     */
    void deleteByCurrentUser();

    /**
     * Count unread notifications for the current user
     */
    long countUnread();
}
