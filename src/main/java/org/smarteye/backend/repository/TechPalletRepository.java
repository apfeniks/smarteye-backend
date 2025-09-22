package org.smarteye.backend.repository;

import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.domain.enums.TechPalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TechPalletRepository extends JpaRepository<TechPallet, Long> {

    Optional<TechPallet> findByRfidUid(String rfidUid);

    boolean existsByRfidUid(String rfidUid);

    long countByStatus(TechPalletStatus status);
}
