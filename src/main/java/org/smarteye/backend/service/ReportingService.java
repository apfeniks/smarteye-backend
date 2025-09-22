package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.domain.enums.MeasurementStatus;
import org.smarteye.backend.repository.DefectRepository;
import org.smarteye.backend.repository.MeasurementRepository;
import org.smarteye.backend.repository.WeightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Простая отчётность (CSV) по измерениям/дефектам за период.
 * Расширяется под требования ТЗ (выгрузка в 1С и т.д.).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportingService {

    private final MeasurementRepository measurementRepository;
    private final DefectRepository defectRepository;
    private final WeightRepository weightRepository;

    /**
     * Генерация CSV с ключевыми полями измерений за период.
     * Формат: ; - разделитель, \n - перевод строки, UTF-8.
     */
    public byte[] generateCsv(OffsetDateTime from, OffsetDateTime to) {
        // На данном этапе используем простой выбор всей таблицы и фильтрацию в коде.
        // При необходимости заменить на кастомный запрос с фильтрацией по created_at.
        List<Measurement> all = measurementRepository.findAll().stream()
                .filter(m -> m.getCreatedAt() != null
                        && (from == null || !m.getCreatedAt().isBefore(from))
                        && (to == null || !m.getCreatedAt().isAfter(to)))
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        // header
        sb.append("measurement_id;status;mode;tech_pallet_id;recipe_id;mass_kg;started_at;finished_at;defects_count\n");

        for (Measurement m : all) {
            long defects = defectRepository.findAllByMeasurementId(m.getId()).size();
            sb.append(m.getId()).append(';')
                    .append(nullSafe(m.getStatus())).append(';')
                    .append(nullSafe(m.getMode())).append(';')
                    .append(m.getTechPallet() != null ? m.getTechPallet().getId() : "").append(';')
                    .append(m.getRecipe() != null ? m.getRecipe().getId() : "").append(';')
                    .append(m.getMassKg() != null ? m.getMassKg() : "").append(';')
                    .append(m.getStartedAt() != null ? m.getStartedAt() : "").append(';')
                    .append(m.getFinishedAt() != null ? m.getFinishedAt() : "").append(';')
                    .append(defects)
                    .append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Краткая сводка: количество измерений по статусам за период.
     */
    public byte[] generateSummaryCsv(OffsetDateTime from, OffsetDateTime to) {
        List<Measurement> all = measurementRepository.findAll().stream()
                .filter(m -> m.getCreatedAt() != null
                        && (from == null || !m.getCreatedAt().isBefore(from))
                        && (to == null || !m.getCreatedAt().isAfter(to)))
                .collect(Collectors.toList());

        long created = all.stream().filter(m -> m.getStatus() == MeasurementStatus.CREATED).count();
        long inProgress = all.stream().filter(m -> m.getStatus() == MeasurementStatus.IN_PROGRESS).count();
        long pending = all.stream().filter(m -> m.getStatus() == MeasurementStatus.PENDING_REVIEW).count();
        long finished = all.stream().filter(m -> m.getStatus() == MeasurementStatus.FINISHED).count();
        long rejected = all.stream().filter(m -> m.getStatus() == MeasurementStatus.REJECTED).count();
        long error = all.stream().filter(m -> m.getStatus() == MeasurementStatus.ERROR).count();

        StringBuilder sb = new StringBuilder();
        sb.append("status;count\n");
        sb.append("CREATED;").append(created).append('\n');
        sb.append("IN_PROGRESS;").append(inProgress).append('\n');
        sb.append("PENDING_REVIEW;").append(pending).append('\n');
        sb.append("FINISHED;").append(finished).append('\n');
        sb.append("REJECTED;").append(rejected).append('\n');
        sb.append("ERROR;").append(error).append('\n');

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String nullSafe(Object o) {
        return o == null ? "" : String.valueOf(o);
    }
}
