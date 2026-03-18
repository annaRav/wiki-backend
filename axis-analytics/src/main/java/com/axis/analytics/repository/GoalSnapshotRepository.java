package com.axis.analytics.repository;

import com.axis.analytics.model.document.GoalSnapshot;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GoalSnapshotRepository implements PanacheMongoRepository<GoalSnapshot> {

    public Optional<GoalSnapshot> findByGoalId(String goalId) {
        return find("goalId", goalId).firstResultOptional();
    }

    public List<GoalSnapshot> findByUserId(String userId) {
        return find("userId", userId).list();
    }
}