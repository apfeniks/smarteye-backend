package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smarteye.backend.config.ConveyorProperties;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.domain.Weight;
import org.smarteye.backend.domain.enums.WeightPhase;
import org.smarteye.backend.repository.WeightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Optional;

/**
 * Конвейерная логика сопоставления показаний весов до/после пресса.
 * Правила:
 *  1) Если есть RFID (т.е. у измерений известен TechPallet) — матчим по techPallet.
 *  2) Иначе — позиционная корреляция по смещению offsetBetweenScales (по умолчанию 5).
 *
 * Хранит ограниченные очереди последних показаний для "BEFORE" и "AFTER".
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConveyorPipelineService {

    private static final int QUEUE_CAP = 10_000; // достаточно для линии (перестраховка)

    private final WeightRepository weightRepository;
    private final MeasurementService measurementService;
    private final ConveyorProperties conveyorProperties;

    /** Очередь последних BEFORE-показаний (id записей Weight). */
    private final Deque<Long> beforeQueue = new ArrayDeque<>(QUEUE_CAP);
    /** Очередь последних AFTER-показаний (id записей Weight) — в основном для диагностики. */
    private final Deque<Long> afterQueue = new ArrayDeque<>(QUEUE_CAP);

    /** Регистрация показания BEFORE в очереди. */
    public void onBefore(Weight before) {
        if (before.getPhase() != WeightPhase.BEFORE) return;
        synchronized (beforeQueue) {
            beforeQueue.addLast(before.getId());
            trim(beforeQueue);
        }
    }

    /**
     * Регистрация показания AFTER и попытка найти парный BEFORE.
     * При успешном сопоставлении перепривязывает AFTER к измерению BEFORE (если это не так),
     * затем просит MeasurementService пересчитать массу.
     */
    public void onAfter(Weight after) {
        if (after.getPhase() != WeightPhase.AFTER) return;

        synchronized (afterQueue) {
            afterQueue.addLast(after.getId());
            trim(afterQueue);
        }

        // 1) попытаемся по RFID (techPallet)
        Measurement afterM = after.getMeasurement();
        TechPallet tp = afterM != null ? afterM.getTechPallet() : null;
        if (tp != null && tryMatchByTechPallet(after, tp)) {
            return;
        }

        // 2) fallback: позиционная корреляция по смещению
        tryMatchByOffset(after);
    }

    // ===== helpers =====

    private void trim(Deque<Long> q) {
        while (q.size() > QUEUE_CAP) {
            q.removeFirst();
        }
    }

    /**
     * Сопоставление по techPallet: ищем ближайший недавний BEFORE с тем же поддоном.
     * Если найдено — привязываем текущий AFTER к измерению того BEFORE.
     */
    private boolean tryMatchByTechPallet(Weight after, TechPallet techPallet) {
        Long matchedBeforeId = null;

        synchronized (beforeQueue) {
            Iterator<Long> it = beforeQueue.descendingIterator();
            while (it.hasNext()) {
                Long beforeId = it.next();
                Weight before = weightRepository.findById(beforeId).orElse(null);
                if (before == null || before.getMeasurement() == null) continue;
                Measurement bm = before.getMeasurement();
                if (bm.getTechPallet() != null && bm.getTechPallet().getId() != null
                        && bm.getTechPallet().getId().equals(techPallet.getId())) {
                    // Проверим, что к этому измерению ещё не привязан AFTER
                    var existingAfter = weightRepository.findFirstByMeasurementIdAndPhase(bm.getId(), WeightPhase.AFTER);
                    if (existingAfter.isEmpty() || existingAfter.get().getId().equals(after.getId())) {
                        matchedBeforeId = beforeId;
                        break;
                    }
                }
            }
        }

        if (matchedBeforeId != null) {
            Weight before = weightRepository.findById(matchedBeforeId).orElse(null);
            if (before != null && before.getMeasurement() != null) {
                reassignAfterToMeasurement(after, before.getMeasurement());
                return true;
            }
        }
        return false;
    }

    /**
     * Сопоставление по позиционному смещению offsetBetweenScales.
     * Берём BEFORE, пришедший на offset позиций раньше.
     */
    private void tryMatchByOffset(Weight after) {
        int offset = Math.max(conveyorProperties.getOffsetBetweenScales(), 0);

        Long beforeId = null;
        synchronized (beforeQueue) {
            if (beforeQueue.size() >= offset && offset > 0) {
                // Получим элемент с конца с отступом offset
                int idxFromTail = offset; // 1 = самый последний BEFORE, offset=5 -> пятый от конца
                int i = 1;
                for (Iterator<Long> it = beforeQueue.descendingIterator(); it.hasNext(); i++) {
                    Long id = it.next();
                    if (i == idxFromTail) {
                        beforeId = id;
                        break;
                    }
                }
            } else {
                log.debug("Offset match skipped: beforeQueue.size={} offset={}", beforeQueue.size(), offset);
            }
        }

        if (beforeId == null) return;

        Optional<Weight> beforeOpt = weightRepository.findById(beforeId);
        if (beforeOpt.isEmpty()) return;

        Weight before = beforeOpt.get();
        Measurement bm = before.getMeasurement();
        if (bm == null) return;

        // Если AFTER уже принадлежит другому измерению — перепривяжем к измерению BEFORE
        reassignAfterToMeasurement(after, bm);
    }

    /** Перепривязка AFTER к измерению и пересчёт массы. */
    private void reassignAfterToMeasurement(Weight after, Measurement targetMeasurement) {
        Measurement current = after.getMeasurement();
        if (current == null || !targetMeasurement.getId().equals(current.getId())) {
            after.setMeasurement(targetMeasurement);
            weightRepository.save(after);
            log.debug("AFTER {} reassigned to measurement {}", after.getId(), targetMeasurement.getId());
        }
        // Пересчитать массу (если есть BEFORE и AFTER)
        measurementService.recomputeMassIfPossible(targetMeasurement.getId());
    }
}
