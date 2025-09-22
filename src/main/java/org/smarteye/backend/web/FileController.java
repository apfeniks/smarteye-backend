package org.smarteye.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarteye.backend.domain.FileRef;
import org.smarteye.backend.domain.enums.StorageType;
import org.smarteye.backend.mapper.FileRefMapper;
import org.smarteye.backend.service.FileService;
import org.smarteye.backend.storage.MinioStorageClient;
import org.smarteye.backend.web.dto.FileDtos.FileCreateRequest;
import org.smarteye.backend.web.dto.FileDtos.FileResponse;
import org.smarteye.backend.web.dto.FileDtos.FileUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileRefMapper fileRefMapper;
    private final MinioStorageClient minio;

    @GetMapping("/{id}")
    public FileResponse get(@PathVariable Long id) {
        return fileRefMapper.toResponse(fileService.getOrThrow(id));
    }

    @PostMapping
    public ResponseEntity<FileResponse> create(@Valid @RequestBody FileCreateRequest req) {
        FileRef entity = fileRefMapper.toEntity(req);
        if (entity.getStorage() == null) {
            entity.setStorage(StorageType.MINIO);
        }
        FileRef saved = fileService.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileRefMapper.toResponse(saved));
    }

    @PatchMapping("/{id}")
    public FileResponse update(@PathVariable Long id, @Valid @RequestBody FileUpdateRequest req) {
        FileRef patch = fileRefMapper.toEntity(new FileCreateRequest(
                // objectKey обязателен только при create — тут не используем
                "placeholder", req.format(), null, req.filename(), req.contentType(), req.sizeBytes(), req.checksum()
        ));
        FileRef updated = fileService.update(id, patch);
        return fileRefMapper.toResponse(updated);
    }

    /** Публичная ссылка на объект (MinIO/S3). */
    @GetMapping("/public-url")
    public String publicUrl(@RequestParam String objectKey) {
        return minio.publicUrl(objectKey);
    }
}
