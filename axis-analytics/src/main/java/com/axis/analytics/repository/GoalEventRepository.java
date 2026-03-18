package com.axis.analytics.repository;

import com.axis.analytics.model.document.GoalEventDocument;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class GoalEventRepository implements PanacheMongoRepository<GoalEventDocument> {

    public List<GoalEventDocument> findByGoalIdOrderByOccurredAt(String goalId) {
        return find("goalId", Sort.by("occurredAt", Sort.Direction.Ascending), goalId).list();
    }

    public List<GoalEventDocument> findByUserId(String userId) {
        return find("userId", userId).list();
    }
}