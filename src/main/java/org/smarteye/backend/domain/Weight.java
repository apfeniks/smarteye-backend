package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.smarteye.backend.domain.enums.WeightPhase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Показание весов для такта: BEFORE (до пресса) / AFTER (после пресса).
 * Может временно существовать без привязки к Measurement (когда корреляция выполняется асинхронно).
 */
@Entity
@Table(name = "weights",
        indexes = {
                @Index(name = "ix_weights_phase", columnList = "phase"),
                @Index(name = "ix_weights_taken_at", columnList = "taken_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Weight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Привязка к измерению (может быть NULL до момента сопоставления). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_id")
    private Measurement measurement;

    /** Фаза взвешивания: BEFORE / AFTER. */
    @Enumerated(EnumType.STRING)
    @Column(name = "phase", nullable = false, length = 16)
    private WeightPhase phase;

    /** Значение веса в килограммах. */
    @Column(name = "value_kg", nullable = false, precision = 12, scale = 3)
    private BigDecimal valueKg;

    /** Идентификатор устройства весов (опционально для трассировки). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    /** Время снятия показания (если поступает от устройства). */
    @Column(name = "taken_at")
    private OffsetDateTime takenAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
