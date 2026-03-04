package com.axis.user.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Theme theme = Theme.SYSTEM;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String locale = "en_US";

    @Column(name = "accent_color", length = 7)
    private String accentColor;

    @Column(name = "two_factor_enabled", nullable = false)
    @Builder.Default
    private boolean twoFactorEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "week_starts_on", nullable = false)
    @Builder.Default
    private WeekStartsOn weekStartsOn = WeekStartsOn.MONDAY;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_goal_view", nullable = false)
    @Builder.Default
    private DefaultGoalView defaultGoalView = DefaultGoalView.LIST;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Theme { LIGHT, DARK, SYSTEM }
    public enum WeekStartsOn { MONDAY, SUNDAY }
    public enum DefaultGoalView { LIST, BOARD, CALENDAR }
}