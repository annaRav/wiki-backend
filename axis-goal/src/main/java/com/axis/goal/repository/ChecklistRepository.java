package com.axis.goal.repository;

import com.axis.goal.model.entity.Checklist;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ChecklistRepository implements PanacheRepositoryBase<Checklist, UUID> {

    public List<Checklist> findByOwnerId(UUID ownerId) {
        return find("ownerId", Sort.by("position"), ownerId).list();
    }

    public long countByOwnerId(UUID ownerId) {
        return count("ownerId", ownerId);
    }
}
