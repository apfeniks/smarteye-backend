package org.smarteye.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarteye.backend.service.PointcloudStorageService;
import org.smarteye.backend.web.dto.PresignRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pointclouds")
public class PointcloudController {

    private final PointcloudStorageService storage;

    @PostMapping("/presign")
    public ResponseEntity<Map<String, String>> presign(@RequestBody @Valid PresignRequest req) throws Exception {
        String method = req.method() != null ? req.method() : "PUT";
        String url = "PUT".equalsIgnoreCase(method)
                ? storage.presignPut(req.objectKey(), 15)
                : storage.presignGet(req.objectKey(), 15);
        return ResponseEntity.ok(Map.of("url", url, "method", method.toUpperCase()));
    }
}
