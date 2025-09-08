package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.repository.TechPalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TechPalletService {
    private final TechPalletRepository repo;

    @Transactional
    public TechPallet create(TechPallet p) {
        p.setCreatedAt(OffsetDateTime.now());
        return repo.save(p);
    }

    public List<TechPallet> list() { return repo.findAll(); }

    @Transactional
    public TechPallet updateStatus(Long id, String status) {
        TechPallet p = repo.findById(id).orElseThrow();
        p.setStatus(status);
        p.setUpdatedAt(OffsetDateTime.now());
        return repo.save(p);
    }
}
