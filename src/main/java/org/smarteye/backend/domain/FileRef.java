package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.smarteye.backend.domain.enums.StorageType;

import java.time.OffsetDateTime;

/**
 * Ссылка на файл в хранилище (MinIO/SMB): облако точек, отчёты и т.п.
 */
@Entity
@Table(name = "files",
        indexes = {
                @Index(name = "ux_files_key", columnList = "object_key", unique = true),
                @Index(name = "ix_files_storage", columnList = "storage")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileRef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ключ/путь объекта в хранилище (например, S3 object key). */
    @Column(name = "object_key", nullable = false, length = 512, unique = true)
    private String objectKey;

    /** Формат содержимого (parquet|ply|csv|pdf и т.п.). */
    @Column(name = "format", length = 32)
    private String format;

    /** Тип хранилища. */
    @Enumerated(EnumType.STRING)
    @Column(name = "storage", nullable = false, length = 16)
    private StorageType storage;

    /** Имя файла (опционально, для отображения). */
    @Column(name = "filename", length = 256)
    private String filename;

    /** MIME-тип (опционально). */
    @Column(name = "content_type", length = 128)
    private String contentType;

    /** Размер в байтах (если известен). */
    @Column(name = "size_bytes")
    private Long sizeBytes;

    /** Контрольная сумма (если известна). */
    @Column(name = "checksum", length = 128)
    private String checksum;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
