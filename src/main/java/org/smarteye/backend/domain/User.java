package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.smarteye.backend.security.model.Role;

import java.time.OffsetDateTime;

/** Пользователь системы. */
@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "ux_users_username", columnList = "username", unique = true)
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Логин (уникальный). */
    @Column(nullable = false, length = 64, unique = true)
    private String username;

    /** BCrypt-хэш пароля. */
    @Column(nullable = false, length = 200)
    private String password;

    /** Роль пользователя. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Role role;

    /** Включён ли пользователь. */
    @Column(nullable = false)
    private boolean enabled = true;

    /** Дата создания. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
