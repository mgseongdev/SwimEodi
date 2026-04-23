package com.swimeodi.controller;

import com.swimeodi.dto.PoolReportDto;
import com.swimeodi.service.PoolReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class PoolReportController {

    private final PoolReportService reportService;

    @PostMapping
    public ResponseEntity<PoolReportDto> create(@RequestBody PoolReportDto dto) {
        return ResponseEntity.ok(reportService.create(dto));
    }

    @GetMapping
    public List<PoolReportDto> getAll(@RequestParam(required = false) String status) {
        if ("PENDING".equals(status)) return reportService.getPending();
        return reportService.getAll();
    }

    @PostMapping("/{id}/done")
    public ResponseEntity<PoolReportDto> markDone(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.markDone(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<PoolReportDto> approve(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.get("adminComment") : null;
        return ResponseEntity.ok(reportService.approve(id, comment));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<PoolReportDto> reject(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.get("adminComment") : null;
        return ResponseEntity.ok(reportService.reject(id, comment));
    }
}
