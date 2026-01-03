package com.axis.goal.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "custom_field_answers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_definition_id", nullable = false)
    private CustomFieldDefinition fieldDefinition; // Ссылка на определение поля

    @Column(name = "field_value", columnDefinition = "TEXT")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;
}
