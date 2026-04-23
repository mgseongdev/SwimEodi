package com.swimeodi.repository;

import com.swimeodi.entity.PoolReportPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoolReportPriceRepository extends JpaRepository<PoolReportPrice, Long> {
    List<PoolReportPrice> findByReportId(Long reportId);
    void deleteByReportId(Long reportId);
}
