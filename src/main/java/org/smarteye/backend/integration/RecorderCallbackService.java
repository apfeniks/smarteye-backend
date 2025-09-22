package org.smarteye.backend.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smarteye.backend.domain.FileRef;
import org.smarteye.backend.domain.enums.StorageType;
import org.smarteye.backend.service.FileService;
import org.smarteye.backend.service.MeasurementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Приём колбэков от Recorder (сканер): завершение измерения, прикрепление файла и метрик.
 * Используется из контроллера /api/v1/measurements/{id}/finish.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecorderCallbackService {

    private final FileService fileService;
    private final MeasurementService measurementService;

    /**
     * Обработка завершения: регистрируем/обновляем FileRef и завершаем Measurement.
     *
     * @param measurementId  ID измерения
     * @param objectKey      ключ объекта в бакете (например, clouds/2025-09-11/m123.parquet)
     * @param format         формат файла: parquet|ply|...
     * @param filename       имя файла (опц.)
     * @param contentType    MIME-тип (опц.)
     * @param sizeBytes      размер (опц.)
     * @param checksum       контрольная сумма (опц.)
     * @param summaryMetrics агрегированные метрики (JSON)
     */
    public void finish(Long measurementId,
                       String objectKey,
                       String format,
                       String filename,
                       String contentType,
                       Long sizeBytes,
                       String checksum,
                       String summaryMetrics) {

        // Найдём/создадим запись о файле
        FileRef file = fileService.findByObjectKey(objectKey).orElseGet(() -> {
            FileRef f = FileRef.builder()
                    .objectKey(objectKey)
                    .format(format)
                    .filename(filename)
                    .contentType(contentType)
                    .sizeBytes(sizeBytes)
                    .checksum(checksum)
                    .storage(StorageType.MINIO)
                    .build();
            return fileService.create(f);
        });

        // Обновим метаданные при необходимости
        file.setFormat(format != null ? format : file.getFormat());
        if (filename != null) file.setFilename(filename);
        if (contentType != null) file.setContentType(contentType);
        if (sizeBytes != null) file.setSizeBytes(sizeBytes);
        if (checksum != null) file.setChecksum(checksum);

        // Завершаем измерение и прикрепляем файл/метрики
        measurementService.finish(measurementId, file, summaryMetrics);
        log.info("Measurement {} finished with file {}", measurementId, objectKey);
    }
}
