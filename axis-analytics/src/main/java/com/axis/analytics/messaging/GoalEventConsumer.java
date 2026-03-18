package com.axis.analytics.messaging;

import com.axis.analytics.service.AnalyticsService;
import com.axis.common.event.GoalDomainEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.concurrent.CompletionStage;

@Slf4j
@ApplicationScoped
public class GoalEventConsumer {

    @Inject
    AnalyticsService analyticsService;

    @Incoming("goal-events-in")
    public CompletionStage<Void> consume(Message<GoalDomainEvent> message) {
        try {
            analyticsService.processEvent(message.getPayload());
            return message.ack();
        } catch (Exception e) {
            log.error("Failed to process goal event", e);
            return message.nack(e);
        }
    }
}