package com.axis.notification.service;

import com.axis.notification.model.dto.PageResponse;
import com.axis.notification.model.dto.NotificationTemplateRequest;
import com.axis.notification.model.dto.NotificationTemplateResponse;
import com.axis.notification.model.entity.NotificationTemplates;

import java.util.UUID;

public interface NotificationTemplatesService {

    /**
     * Create a new notification template
     */
    NotificationTemplateResponse create(NotificationTemplateRequest request);

    /**
     * Update an existing notification template
     */
    NotificationTemplateResponse update(UUID id, NotificationTemplateRequest request);

    /**
     * Find notification template by ID
     */
    NotificationTemplateResponse findById(UUID id);

    /**
     * Find notification template by type
     */
    NotificationTemplateResponse findByType(NotificationTemplates.Type type);

    /**
     * Find all notification templates with pagination
     */
    PageResponse<NotificationTemplateResponse> findAll(int page, int size, String sortBy, String sortDirection);

    /**
     * Delete notification template by ID
     */
    void deleteById(UUID id);
}
