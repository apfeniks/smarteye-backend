package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.smarteye.backend.domain.enums.MeasurementMode;
import org.smarteye.backend.domain.enums.MeasurementStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Измерение (снятие облака точек для техподдона/такта).
 * Может существовать без techPallet (режим без RFID).
 */
@Entity
@Table(name = "measurements",
        indexes = {
                @Index(name = "ix_measurements_status", columnList = "status"),
                @Index(name = "ix_measurements_created_at", columnList = "created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Технологический поддон (может быть NULL в режиме без RFID). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_pallet_id")
    private TechPallet techPallet;

    /** Рецепт (опционально). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    /** Устройство-инициатор (сканер/ESP32), опционально. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    /** Файл с облаком точек/результатом (parquet/ply), устанавливается на finish. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private FileRef file;

    /** Режим запуска (AUTO/SILENT/OPERATOR). */
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 32)
    private MeasurementMode mode;

    /** Статус жизненного цикла измерения. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private MeasurementStatus status;

    /** Код проблемы/особое примечание (при разрывах позиции/нет RFID и т.п.). */
    @Column(name = "issue_code", length = 64)
    private String issueCode;

    /** Итоговая масса продукции (kg) = AFTER - BEFORE, если доступны оба веса. */
    @Column(name = "mass_kg", precision = 12, scale = 3)
    private BigDecimal massKg;

    /** Произвольные агрегированные метрики сканирования (JSON строка). */
    @Column(name = "summary_metrics", columnDefinition = "TEXT")
    private String summaryMetrics;

    /** Временные метки начала/завершения измерения. */
    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
