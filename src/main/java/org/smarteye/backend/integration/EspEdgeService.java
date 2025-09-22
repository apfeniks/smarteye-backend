package org.smarteye.backend.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smarteye.backend.domain.Device;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.domain.enums.MeasurementMode;
import org.smarteye.backend.service.DeviceService;
import org.smarteye.backend.service.MeasurementService;
import org.smarteye.backend.service.TechPalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Интеграция с краевыми контроллерами (ESP32 и др.).
 * Даёт упрощённые методы запуска измерения с/без RFID.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EspEdgeService {

    private final MeasurementService measurementService;
    private final TechPalletService techPalletService;
    private final DeviceService deviceService;

    /**
     * Явный старт измерения.
     *
     * @param deviceCode код устройства-инициатора (например, "ESP32-01")
     * @param rfidUid    опциональный RFID техподдона (может быть null/пустой)
     * @param recipeId   опциональный рецепт
     * @param mode       режим (если null -> AUTO)
     * @return созданное измерение
     */
    public Measurement start(String deviceCode, String rfidUid, Long recipeId, MeasurementMode mode) {
        Device device = deviceService.getByCodeOrThrow(deviceCode);

        Measurement m = new Measurement();
        if (rfidUid != null && !rfidUid.isBlank()) {
            Optional<TechPallet> p = techPalletService.findByRfid(rfidUid);
            p.ifPresent(m::setTechPallet);
        }
        if (recipeId != null) {
            var r = new org.smarteye.backend.domain.Recipe();
            r.setId(recipeId);
            m.setRecipe(r);
        }
        m.setDevice(device);

        return measurementService.start(m, mode != null ? mode : MeasurementMode.AUTO);
    }
}
