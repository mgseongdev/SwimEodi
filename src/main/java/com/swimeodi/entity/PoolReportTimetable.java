package com.swimeodi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pool_report_timetable")
@Getter
@Setter
public class PoolReportTimetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private PoolReport report;

    @Column(nullable = false)
    private String dayOfWeek;

    @Column(nullable = false)
    private Integer sessionNo;

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;
}
