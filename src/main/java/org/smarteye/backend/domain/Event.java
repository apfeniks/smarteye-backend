package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Аудит-события (журнал).
 */
@Entity
@Table(name = "events",
        indexes = {
                @Index(name = "ix_events_type", columnList = "type"),
                @Index(name = "ix_events_level", columnList = "level"),
                @Index(name = "ix_events_measurement_id", columnList = "measurement_id"),
                @Index(name = "ix_events_created_at", columnList = "created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Тип события (машиночитаемый). */
    @Column(name = "type", nullable = false, length = 64)
    private String type;

    /** Уровень: INFO/WARN/ERROR. */
    @Column(name = "level", nullable = false, length = 16)
    private String level;

    /** Сообщение. */
    @Column(name = "message", length = 1024)
    private String message;

    /** Доп. данные (строка, обычно JSON). */
    @Column(name = "data", columnDefinition = "TEXT")
    private String data;

    /** Связь с измерением (если применимо). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_id")
    private Measurement measurement;

    /** Корреляционный идентификатор запроса. */
    @Column(name = "request_id", length = 64)
    private String requestId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
