package com.swimeodi.controller;

import com.swimeodi.dto.kakao.KakaoPreviewItem;
import com.swimeodi.service.KakaoImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class BulkImportController {

    private final KakaoImportService kakaoImportService;

    @GetMapping("/preview")
    public ResponseEntity<List<KakaoPreviewItem>> preview(@RequestParam String query) {
        return ResponseEntity.ok(kakaoImportService.preview(query));
    }

    @PostMapping("/import/selected")
    public ResponseEntity<Map<String, Integer>> importSelected(@RequestBody List<KakaoPreviewItem> items) {
        int count = kakaoImportService.importSelected(items);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping(value = "/import/kakao", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter importAll() {
        SseEmitter emitter = new SseEmitter(600_000L);
        new Thread(() -> kakaoImportService.importAll(emitter)).start();
        return emitter;
    }
}
