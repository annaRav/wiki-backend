package com.axis.goal.repository;

import com.axis.goal.model.entity.ChecklistItem;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ChecklistItemRepository implements PanacheRepositoryBase<ChecklistItem, UUID> {

    public List<ChecklistItem> findByChecklistId(UUID checklistId) {
        return find("checklist.id", Sort.by("position"), checklistId).list();
    }

    public Optional<ChecklistItem> findByIdAndChecklistId(UUID id, UUID checklistId) {
        return find("id = ?1 and checklist.id = ?2", id, checklistId).firstResultOptional();
    }

    public long countByChecklistId(UUID checklistId) {
        return count("checklist.id", checklistId);
    }
}
