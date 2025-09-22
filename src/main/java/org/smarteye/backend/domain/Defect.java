package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Дефект/несоответствие, выявленное по метрикам сканирования или оператором.
 */
@Entity
@Table(name = "defects",
        indexes = {
                @Index(name = "ix_defects_measurement_id", columnList = "measurement_id"),
                @Index(name = "ix_defects_code", columnList = "code")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Defect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ссылка на измерение. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_id", nullable = false)
    private Measurement measurement;

    /** Машино-читаемый код дефекта (например, HEIGHT_OUT_OF_TOLERANCE). */
    @Column(name = "code", nullable = false, length = 64)
    private String code;

    /** Человеко-читаемое описание. */
    @Column(name = "description", length = 512)
    private String description;

    /** Доп. данные (JSON строка). */
    @Column(name = "data", columnDefinition = "TEXT")
    private String data;

    /** Источник: AUTO (по правилам) / OPERATOR (вручную). */
    @Column(name = "source", length = 16)
    private String source;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
