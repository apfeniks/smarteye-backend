package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smarteye.backend.domain.Device;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.domain.Weight;
import org.smarteye.backend.domain.enums.WeightPhase;
import org.smarteye.backend.repository.WeightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Приём и сохранение показаний весов + запуск конвейерной корреляции.
 * Поддержка режимов: с RFID (привяжем к tech_pallet), без RFID (создадим измерение на лету).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WeightsService {

    private final WeightRepository weightRepository;
    private final TechPalletService techPalletService;
    private final MeasurementService measurementService;
    private final ConveyorPipelineService conveyorPipelineService;

    /**
     * Запись показания веса и запуск логики сопоставления.
     *
     * @param weight    сущность веса (phase, value, device, takenAt, measurement?).
     * @param rfidUid   опциональный RFID, считанный на посту (может быть null/пустой).
     * @return сохранённый вес
     */
    public Weight record(Weight weight, String rfidUid) {
        // 1) если measurement не задан — пытаемся определить
        if (weight.getMeasurement() == null) {
            weight.setMeasurement(resolveOrCreateMeasurement(weight, rfidUid));
        }

        // 2) сохраняем вес
        Weight saved = weightRepository.save(weight);

        // 3) отправляем в конвейерную логику
        if (saved.getPhase() == WeightPhase.BEFORE) {
            conveyorPipelineService.onBefore(saved);
        } else {
            conveyorPipelineService.onAfter(saved);
        }

        // 4) если у измерения известны обе фазы — посчитать массу
        Measurement m = saved.getMeasurement();
        if (m != null && m.getId() != null) {
            measurementService.recomputeMassIfPossible(m.getId());
        }

        return saved;
    }

    /** Определяем/создаём Measurement, чтобы можно было связать BEFORE/AFTER даже без RFID. */
    private Measurement resolveOrCreateMeasurement(Weight weight, String rfidUid) {
        // Попытка привязаться по RFID к tech_pallet (режим с RFID)
        if (rfidUid != null && !rfidUid.isBlank()) {
            Optional<TechPallet> palletOpt = techPalletService.findByRfid(rfidUid);
            if (palletOpt.isPresent()) {
                Device device = weight.getDevice();
                return measurementService.autoStart(palletOpt.get(), device);
            } else {
                log.warn("RFID {} not found in tech_pallets, starting measurement without pallet", rfidUid);
            }
        }

        // Режим без RFID: создаём измерение «на лету» (без техподдона), чтобы связать пары весов
        Device device = weight.getDevice();
        return measurementService.autoStart(null, device);
    }
}
