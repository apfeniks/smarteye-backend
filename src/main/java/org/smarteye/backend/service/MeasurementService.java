package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.common.audit.AuditLogger;
import org.smarteye.backend.common.exception.NotFoundException;
import org.smarteye.backend.common.util.TimeUtil;
import org.smarteye.backend.domain.*;
import org.smarteye.backend.domain.enums.MeasurementMode;
import org.smarteye.backend.domain.enums.MeasurementStatus;
import org.smarteye.backend.repository.MeasurementRepository;
import org.smarteye.backend.repository.WeightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Управление жизненным циклом измерений:
 * start → (weights BEFORE/AFTER) → finish (+file/metrics) → operator decision (в другом сервисе).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final WeightRepository weightRepository;
    private final AuditLogger audit;

    @Transactional(readOnly = true)
    public Measurement getOrThrow(Long id) {
        return measurementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Measurement not found: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<Measurement> listByStatus(MeasurementStatus status) {
        return measurementRepository.findAllByStatus(status);
    }

    /** Явный старт измерения внешним устройством/службой. */
    public Measurement start(Measurement m, MeasurementMode mode) {
        m.setMode(mode != null ? mode : MeasurementMode.AUTO);
        m.setStatus(MeasurementStatus.CREATED);
        m.setStartedAt(TimeUtil.nowUtc());
        Measurement saved = measurementRepository.save(m);
        audit.info("MEASUREMENT_CREATED", "measurement started", null, saved.getId());
        return saved;
    }

    /** Упрощённый автостарт (без RFID возможен), если пришли веса без measurementId. */
    public Measurement autoStart(TechPallet techPallet, Device device) {
        Measurement m = new Measurement();
        m.setTechPallet(techPallet);
        m.setDevice(device);
        m.setMode(MeasurementMode.AUTO);
        m.setStatus(MeasurementStatus.IN_PROGRESS);
        m.setStartedAt(TimeUtil.nowUtc());
        Measurement saved = measurementRepository.save(m);
        audit.info("MEASUREMENT_AUTOSTART", "auto start from weight", null, saved.getId());
        return saved;
    }

    /** Завершение измерения — присвоение файла и метрик. */
    public Measurement finish(Long id, FileRef file, String summaryMetrics) {
        Measurement m = getOrThrow(id);
        m.setFile(file);
        m.setSummaryMetrics(summaryMetrics);
        m.setStatus(MeasurementStatus.PENDING_REVIEW); // далее решает оператор/правила
        m.setFinishedAt(TimeUtil.nowUtc());
        audit.info("MEASUREMENT_FINISHED", "measurement finished (file attached)", null, m.getId());
        return m;
    }

    /** Перевод в FINISHED (после принятия решения, если требуется). */
    public Measurement markFinished(Long id) {
        Measurement m = getOrThrow(id);
        m.setStatus(MeasurementStatus.FINISHED);
        audit.info("MEASUREMENT_STATUS", "status set to FINISHED", null, m.getId());
        return m;
    }

    /** Перевод в REJECTED. */
    public Measurement markRejected(Long id, String reason) {
        Measurement m = getOrThrow(id);
        m.setStatus(MeasurementStatus.REJECTED);
        m.setIssueCode(reason);
        audit.warn("MEASUREMENT_REJECTED", "measurement rejected by operator", null, m.getId());
        return m;
    }

    /** Если доступны обе фазы веса — рассчитываем массу продукции. */
    public void recomputeMassIfPossible(Long measurementId) {
        var beforeOpt = weightRepository.findFirstByMeasurementIdAndPhase(measurementId, org.smarteye.backend.domain.enums.WeightPhase.BEFORE);
        var afterOpt = weightRepository.findFirstByMeasurementIdAndPhase(measurementId, org.smarteye.backend.domain.enums.WeightPhase.AFTER);
        if (beforeOpt.isPresent() && afterOpt.isPresent()) {
            Measurement m = getOrThrow(measurementId);
            BigDecimal mass = afterOpt.get().getValueKg().subtract(beforeOpt.get().getValueKg());
            m.setMassKg(mass);
            if (m.getStatus() == MeasurementStatus.CREATED) {
                m.setStatus(MeasurementStatus.IN_PROGRESS);
            }
            audit.info("MEASUREMENT_MASS", "mass computed (kg)", null, m.getId());
        }
    }

    /** Привязать файл к измерению (используется Recorder'ом). */
    public Measurement attachFile(Long id, FileRef file) {
        Measurement m = getOrThrow(id);
        m.setFile(file);
        return m;
    }

    /** Установить агрегированные метрики (JSON). */
    public Measurement setSummaryMetrics(Long id, String json) {
        Measurement m = getOrThrow(id);
        m.setSummaryMetrics(json);
        return m;
    }

    /** Пометить ошибку процесса. */
    public Measurement markError(Long id, String issueCode) {
        Measurement m = getOrThrow(id);
        m.setStatus(MeasurementStatus.ERROR);
        m.setIssueCode(issueCode);
        audit.error("MEASUREMENT_ERROR", issueCode, null, m.getId());
        return m;
    }

    // Вспомогательно
    @Transactional(readOnly = true)
    public Optional<Measurement> tryGet(Long id) {
        return measurementRepository.findById(id);
    }
}
