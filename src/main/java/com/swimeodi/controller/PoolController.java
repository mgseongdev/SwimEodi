package com.swimeodi.controller;

import com.swimeodi.dto.PoolDto;
import com.swimeodi.service.PoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pools")
@RequiredArgsConstructor
public class PoolController {

    private final PoolService poolService;

    @GetMapping
    public ResponseEntity<List<PoolDto>> getPools(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(poolService.searchByName(name));
        }
        if (address != null && !address.isBlank()) {
            return ResponseEntity.ok(poolService.searchByAddress(address));
        }
        return ResponseEntity.ok(poolService.getAllPools());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PoolDto> getPool(@PathVariable Long id) {
        return ResponseEntity.ok(poolService.getPool(id));
    }

    @PostMapping
    public ResponseEntity<PoolDto> createPool(@RequestBody PoolDto dto) {
        return ResponseEntity.ok(poolService.createPool(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PoolDto> updatePool(@PathVariable Long id, @RequestBody PoolDto dto) {
        return ResponseEntity.ok(poolService.updatePool(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePool(
            @PathVariable Long id,
            @RequestBody(required = false) PoolDto dto) {
        poolService.deletePool(id, dto != null ? dto.getAdminComment() : null);
        return ResponseEntity.noContent().build();
    }
}
