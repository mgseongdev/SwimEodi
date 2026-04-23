package com.swimeodi.repository;

import com.swimeodi.entity.PoolReport;
import com.swimeodi.entity.PoolReport.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoolReportRepository extends JpaRepository<PoolReport, Long> {
    List<PoolReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);
    List<PoolReport> findAllByOrderByCreatedAtDesc();
}
