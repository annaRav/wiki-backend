package com.axis.goal.repository;

import com.axis.goal.model.entity.Checklist;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ChecklistRepository implements PanacheRepositoryBase<Checklist, UUID> {

    public List<Checklist> findByGoalId(UUID goalId) {
        return find("goal.id", Sort.by("position"), goalId).list();
    }

    public Optional<Checklist> findByIdAndGoalId(UUID id, UUID goalId) {
        return find("id = ?1 and goal.id = ?2", id, goalId).firstResultOptional();
    }

    public long countByGoalId(UUID goalId) {
        return count("goal.id", goalId);
    }

    public boolean existsByIdAndGoalId(UUID id, UUID goalId) {
        return count("id = ?1 and goal.id = ?2", id, goalId) > 0;
    }
}
