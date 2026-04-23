package com.swimeodi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pool_report_prices")
@Getter
@Setter
public class PoolReportPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private PoolReport report;

    @Column(nullable = false)
    private String ticketType;

    private String dayType;
    private String ageGroup;
    private Integer price;
    private String note;
}
