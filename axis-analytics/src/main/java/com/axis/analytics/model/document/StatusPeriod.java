package com.axis.analytics.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusPeriod {
    private String status;
    private Instant enteredAt;
    private Instant exitedAt;
    private Long durationMs;
}