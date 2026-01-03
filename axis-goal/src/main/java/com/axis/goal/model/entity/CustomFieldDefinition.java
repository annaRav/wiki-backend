package com.axis.goal.model.entity;

import com.axis.goal.model.enums.CustomFieldType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "custom_field_definitions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomFieldType type;

    @Column(nullable = false)
    private boolean required;

    private String placeholder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_type_id", nullable = false)
    private GoalType goalType;
}
