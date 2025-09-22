package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.common.exception.NotFoundException;
import org.smarteye.backend.common.exception.ValidationException;
import org.smarteye.backend.common.util.TimeUtil;
import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.domain.enums.TechPalletStatus;
import org.smarteye.backend.repository.TechPalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Бизнес-логика по технологическим поддонам (tech_pallets).
 * Поддерживает сценарий реюза RFID: перенос метки со старого поддона на новый.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TechPalletService {

    private final TechPalletRepository techPalletRepository;

    /** Получить поддон или 404. */
    @Transactional(readOnly = true)
    public TechPallet getOrThrow(Long id) {
        return techPalletRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tech pallet not found: id=" + id));
    }

    /** Поиск по RFID (опционально). */
    @Transactional(readOnly = true)
    public Optional<TechPallet> findByRfid(String rfidUid) {
        return techPalletRepository.findByRfidUid(rfidUid);
    }

    /** Создание нового поддона. Статус по умолчанию ACTIVE. */
    public TechPallet create(TechPallet pallet) {
        if (pallet.getStatus() == null) {
            pallet.setStatus(TechPalletStatus.ACTIVE);
        }
        return techPalletRepository.save(pallet);
    }

    /** Сохранение/обновление. */
    public TechPallet save(TechPallet pallet) {
        return techPalletRepository.save(pallet);
    }

    /** Списание поддона. */
    public TechPallet decommission(Long id, String note) {
        TechPallet p = getOrThrow(id);
        p.setStatus(TechPalletStatus.DECOMMISSIONED);
        p.setDecommissionedAt(TimeUtil.nowUtc());
        if (note != null && !note.isBlank()) {
            String old = p.getNote();
            p.setNote((old == null ? "" : (old + " | ")) + note);
        }
        return p;
    }

    /**
     * Реюз RFID: перенос метки со списанного (или действующего) поддона на новый.
     * Шаги:
     *  1) Считать RFID со старого, очистить у старого поле rfid_uid (чтобы не нарушать UNIQUE), проставить DECOMMISSIONED.
     *  2) У нового проставить этот RFID, previousTechPallet = старый.
     *
     * @param fromPalletId id поддона-источника (откуда забираем метку)
     * @param newPallet новый поддон (без id), в который переносим метку
     * @return созданный новый поддон с присвоенным RFID и ссылкой на предыдущий
     */
    public TechPallet transferRfid(Long fromPalletId, TechPallet newPallet) {
        TechPallet old = getOrThrow(fromPalletId);
        String rfid = old.getRfidUid();
        if (rfid == null || rfid.isBlank()) {
            throw new ValidationException("Old tech pallet has no RFID to transfer");
        }

        // Шаг 1: снимаем метку со старого, списываем
        old.setRfidUid(null);
        old.setStatus(TechPalletStatus.DECOMMISSIONED);
        old.setDecommissionedAt(TimeUtil.nowUtc());
        techPalletRepository.save(old);

        // Шаг 2: создаём новый и присваиваем метку
        if (newPallet.getId() != null) {
            throw new ValidationException("New tech pallet must not have id");
        }
        newPallet.setRfidUid(rfid);
        newPallet.setPreviousTechPallet(old);
        if (newPallet.getStatus() == null) {
            newPallet.setStatus(TechPalletStatus.ACTIVE);
        }
        return techPalletRepository.save(newPallet);
    }
    @Transactional(readOnly = true)
    public java.util.List<TechPallet> findAll() {
        return techPalletRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Optional<TechPallet> tryGet(Long id) {
        if (id == null) return Optional.empty();
        return techPalletRepository.findById(id);
    }
}
