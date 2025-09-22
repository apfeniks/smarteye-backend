package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.common.exception.NotFoundException;
import org.smarteye.backend.domain.FileRef;
import org.smarteye.backend.repository.FileRefRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRefRepository fileRefRepository;
    private final S3Client s3Client;
    private final String s3BucketName;

    @Transactional(readOnly = true)
    public FileRef getOrThrow(Long id) {
        return fileRefRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File not found: id=" + id));
    }

    @Transactional(readOnly = true)
    public Optional<FileRef> findByObjectKey(String key) {
        return fileRefRepository.findByObjectKey(key);
    }

    /** Создаёт запись о файле. Фактическая загрузка выполняется внешним сервисом (Recorder/устройство). */
    public FileRef create(FileRef file) {
        return fileRefRepository.save(file);
    }

    /** Обновляет метаданные файла. */
    public FileRef update(Long id, FileRef patch) {
        FileRef f = getOrThrow(id);
        if (patch.getFilename() != null) f.setFilename(patch.getFilename());
        if (patch.getFormat() != null) f.setFormat(patch.getFormat());
        if (patch.getContentType() != null) f.setContentType(patch.getContentType());
        if (patch.getChecksum() != null) f.setChecksum(patch.getChecksum());
        if (patch.getSizeBytes() != null) f.setSizeBytes(patch.getSizeBytes());
        return f;
    }

    /** Быстрая проверка, что объект существует в бакете. */
    @Transactional(readOnly = true)
    public boolean objectExists(String objectKey) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(s3BucketName)
                    .key(objectKey)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
