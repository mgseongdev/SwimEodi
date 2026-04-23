package com.swimeodi.repository;

import com.swimeodi.entity.PoolReportTimetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoolReportTimetableRepository extends JpaRepository<PoolReportTimetable, Long> {
    List<PoolReportTimetable> findByReportId(Long reportId);
    void deleteByReportId(Long reportId);
}
