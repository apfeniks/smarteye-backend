package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.smarteye.backend.domain.enums.TechPalletStatus;

import java.time.OffsetDateTime;

/**
 * Технологический поддон (tech_pallet).
 * RFID может отсутствовать (режим без RFID).
 * При реюзе RFID создаётся новая запись и ссылка на предыдущую через previousTechPallet.
 */
@Entity
@Table(name = "tech_pallets",
        indexes = {
                @Index(name = "ix_tech_pallets_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_tech_pallets_rfid_uid", columnNames = {"rfid_uid"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechPallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** RFID UID (может быть NULL в режиме без RFID). */
    @Column(name = "rfid_uid", length = 64, unique = true)
    private String rfidUid;

    /** Статус поддона: ACTIVE / DECOMMISSIONED. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TechPalletStatus status;

    /** Дата ввода в работу. */
    @Column(name = "commissioned_at")
    private OffsetDateTime commissionedAt;

    /** Дата вывода из работы/списания. */
    @Column(name = "decommissioned_at")
    private OffsetDateTime decommissionedAt;

    /** Ссылка на предыдущую запись (при переустановке метки на этот же физический поддон). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_tech_pallet_id")
    private TechPallet previousTechPallet;

    /** Произвольные пометки. */
    @Column(name = "note")
    private String note;

    /** Продукт/цвет/рецепт — опционально (для удобства поиска, не обязательно заполнять). */
    @Column(name = "product_code", length = 64)
    private String productCode;

    @Column(name = "color_code", length = 64)
    private String colorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
