package com.axis.goal.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "goal_types", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "level_number"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "level_number", nullable = false)
    private Integer levelNumber;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToMany(mappedBy = "goalType", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CustomFieldDefinition> customFields = new ArrayList<>();

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Goal> goals = new ArrayList<>();
}
