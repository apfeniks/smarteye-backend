package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.repository.MeasurementRepository;
import org.smarteye.backend.repository.TechPalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class MeasurementService {
    private final MeasurementRepository repo;
    private final TechPalletRepository pallets;

    @Transactional
    public Measurement create(Long techPalletId, Integer profilesCount, String meta) {
        var tp = pallets.findById(techPalletId).orElseThrow(() ->
                new IllegalArgumentException("Tech pallet not found: " + techPalletId));

        var m = Measurement.builder()
                .techPallet(tp)
                .profilesCount(profilesCount)
                .status("CREATED")
                .meta(meta)
                .createdAt(OffsetDateTime.now())
                .build();
        m = repo.save(m);

        // не блокируем создание измерения, если что-то пойдёт не так при отправке события
        try {
            events.sendCreated(new MeasurementCreatedEvent(
                    m.getId(), tp.getId(), m.getProfilesCount(), m.getStatus(), m.getCreatedAt()
            ));
        } catch (Exception ex) {
            // можно залогировать WARN
            System.err.println("WARN: failed to send Kafka event: " + ex.getMessage());
        }
        return m;
    }


    public List<Measurement> list() { return repo.findAll(); }
}
