package com.axis.analytics.model.document;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import java.time.Instant;
import java.util.Map;

@MongoEntity(collection = "goal_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalEventDocument {
    public ObjectId id;
    public String eventId;
    public String eventType;
    public String entityType;
    public String entityId;
    public String goalId;
    public String userId;
    public String previousStatus;
    public String newStatus;
    public String title;
    public String description;
    public String lifeAspectId;
    public Map<String, Object> changes;
    public Instant occurredAt;
}