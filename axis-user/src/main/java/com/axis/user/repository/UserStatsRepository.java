package com.axis.user.repository;

import com.axis.user.model.entity.UserStats;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserStatsRepository implements PanacheRepositoryBase<UserStats, UUID> {

    public Optional<UserStats> findByUserId(UUID userId) {
        return find("userId", userId).firstResultOptional();
    }
}