package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Рецепт/технологическая карта изделия.
 * Минимальный набор полей: уникальный код и имя.
 * Доп. поля можно расширять под ТЗ (пороги дефектов, высота и т.п.).
 */
@Entity
@Table(name = "recipes",
        indexes = {
                @Index(name = "ux_recipes_code", columnList = "code", unique = true),
                @Index(name = "ix_recipes_product_code", columnList = "product_code")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Уникальный код рецепта. */
    @Column(nullable = false, length = 64, unique = true)
    private String code;

    /** Человекочитаемое имя рецепта. */
    @Column(nullable = false, length = 128)
    private String name;

    /** Принадлежность к изделию (для быстрого фильтра). */
    @Column(name = "product_code", length = 64)
    private String productCode;

    /** Описание/примечания. */
    @Column(length = 1024)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
