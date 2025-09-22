package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Устройство/узел линии (сканер, весовой пост, ESP32-контроллер и т.п.).
 * Минимальный состав полей: уникальный код, имя/описание, адрес/активность.
 */
@Entity
@Table(name = "devices",
        indexes = {
                @Index(name = "ux_devices_code", columnList = "code", unique = true),
                @Index(name = "ix_devices_active", columnList = "active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Уникальный код устройства (символьный идентификатор). */
    @Column(nullable = false, length = 64, unique = true)
    private String code;

    /** Человекочитаемое имя устройства. */
    @Column(nullable = false, length = 128)
    private String name;

    /** Тип/класс устройства (произвольная строка: SCANNER, SCALE_BEFORE, SCALE_AFTER, ESP32 и т.д.). */
    @Column(name = "device_type", length = 64)
    private String deviceType;

    /** Описание/комментарий. */
    @Column(length = 512)
    private String description;

    /** Сетевой адрес (если применимо). */
    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    /** Активно ли устройство. */
    @Column(nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
