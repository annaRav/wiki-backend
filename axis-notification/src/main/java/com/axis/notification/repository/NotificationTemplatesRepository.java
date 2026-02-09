package com.axis.notification.repository;

import com.axis.notification.model.entity.NotificationTemplates;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class NotificationTemplatesRepository implements PanacheRepositoryBase<NotificationTemplates, UUID> {

    /**
     * Find notification template by type
     */
    public Optional<NotificationTemplates> findByType(NotificationTemplates.Type type) {
        return find("type", type).firstResultOptional();
    }

    /**
     * Check if a template with the given type already exists
     */
    public boolean existsByType(NotificationTemplates.Type type) {
        return count("type", type) > 0;
    }

    /**
     * Find all templates with pagination
     */
    public List<NotificationTemplates> findAll(Page page, Sort sort) {
        return findAll(sort).page(page).list();
    }

    /**
     * Count all templates
     */
    public long countAll() {
        return count();
    }
}
