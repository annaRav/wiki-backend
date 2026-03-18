package com.axis.goal.messaging;

import com.axis.common.event.GoalDomainEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Slf4j
@ApplicationScoped
public class GoalEventPublisher {

    @Inject
    @Channel("goal-events-out")
    Emitter<GoalDomainEvent> emitter;

    public void publish(GoalDomainEvent event) {
        try {
            emitter.send(event);
            log.debug("Published event: {} for entity: {}", event.eventType(), event.entityId());
        } catch (Exception e) {
            log.error("Failed to publish goal event: {}", event.eventType(), e);
        }
    }
}