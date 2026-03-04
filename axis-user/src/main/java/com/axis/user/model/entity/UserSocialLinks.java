package com.axis.user.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_social_links")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialLinks {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "telegram_username", length = 100)
    private String telegramUsername;

    @Column(name = "telegram_chat_id")
    private Long telegramChatId;

    @Column(length = 255)
    private String email;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}