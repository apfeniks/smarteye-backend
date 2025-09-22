package org.smarteye.backend.web;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.service.ReportingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    /** Полный CSV по измерениям за период. */
    @GetMapping(value = "/measurements.csv", produces = "text/csv")
    public ResponseEntity<byte[]> measurementsCsv(@RequestParam(required = false) String from,
                                                  @RequestParam(required = false) String to) {
        OffsetDateTime fromTs = from != null ? OffsetDateTime.parse(from) : null;
        OffsetDateTime toTs = to != null ? OffsetDateTime.parse(to) : null;

        byte[] csv = reportingService.generateCsv(fromTs, toTs);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"measurements.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    /** Краткая сводка по статусам. */
    @GetMapping(value = "/summary.csv", produces = "text/csv")
    public ResponseEntity<byte[]> summaryCsv(@RequestParam(required = false) String from,
                                             @RequestParam(required = false) String to) {
        OffsetDateTime fromTs = from != null ? OffsetDateTime.parse(from) : null;
        OffsetDateTime toTs = to != null ? OffsetDateTime.parse(to) : null;

        byte[] csv = reportingService.generateSummaryCsv(fromTs, toTs);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"summary.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }
}
