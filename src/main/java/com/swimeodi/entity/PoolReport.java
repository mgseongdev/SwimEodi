package com.swimeodi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pool_reports")
@Getter
@Setter
public class PoolReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType = ReportType.NEW;

    // 수정 요청일 때 원본 수영장 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id")
    private Pool pool;

    // 기본 정보
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private Double lat;
    private Double lng;
    private String phone;
    private String website;
    private String description;

    // 운영 정보
    private String operatingHours;
    private String operatingHoursNote;
    private Boolean freeSwimmingAvailable = false;
    private Boolean priceNoteFlag = false;
    @Column(columnDefinition = "TEXT")
    private String priceNote;
    private Boolean timetableNoteFlag = false;
    @Column(columnDefinition = "TEXT")
    private String timetableNote;

    // 시설 정보
    private String poolType;
    private String facilityType;
    private String locationType;
    private Integer laneLength;
    private Integer laneCount;
    private String depth;
    private Integer capacity;
    private Boolean hasBabyPool;
    private Boolean equipmentAvailable = false;

    // 편의시설
    private Boolean hasTowelService;
    private Boolean hasHairDryer;
    private String hairDryerFee;
    private Boolean parkingAvailable;
    private String parkingFee;
    private String showerCleanliness;

    // 제보자 메모
    @Column(columnDefinition = "TEXT")
    private String reporterNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String adminComment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum ReportType { NEW, CORRECTION, HIDE }
    public enum ReportStatus { PENDING, DONE, REJECTED }
}
