package com.swimeodi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "swimming_pools")
@Getter
@Setter
@NoArgsConstructor
public class Pool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    private String phone;

    @Column(name = "operating_hours")
    private String operatingHours;

    private String price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "operating_hours_note")
    private String operatingHoursNote;

    // 수영장 위치 (지상 / 지하)
    @Column(name = "location_type")
    private String locationType;

    // 레인 길이 (단위: m, 예: 25, 50)
    @Column(name = "lane_length")
    private Integer laneLength;

    // 레인 수
    @Column(name = "lane_count")
    private Integer laneCount;

    // 유아풀 유무
    @Column(name = "has_baby_pool")
    private Boolean hasBabyPool;

    // 샤워실 청결도 (매우청결 / 청결 / 보통 / 불량)
    @Column(name = "shower_cleanliness")
    private String showerCleanliness;

    // 수건 제공 여부
    @Column(name = "has_towel_service")
    private Boolean hasTowelService;

    // 드라이기 비치 여부
    @Column(name = "has_hair_dryer")
    private Boolean hasHairDryer;

    // 드라이기 사용료 (예: 무료, 500원)
    @Column(name = "hair_dryer_fee")
    private String hairDryerFee;

    // 주차 가능 여부
    @Column(name = "parking_available")
    private Boolean parkingAvailable;

    // 주차료 (예: 무료, 30분 1,000원)
    @Column(name = "parking_fee")
    private String parkingFee;

    // 수심 (예: 1.2m, 1.0~1.6m)
    private String depth;

    // 정원 (명)
    private Integer capacity;

    // 자유수영 운영 여부
    @Column(name = "free_swimming_available", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean freeSwimmingAvailable = false;

    // 운영 주체 유형 (공공 / 사설)
    @Column(name = "pool_type")
    private String poolType;

    // 시설 유형 (호텔 / 리조트 / 학교 / 아파트 등 — 확장 가능)
    @Column(name = "facility_type")
    private String facilityType;

    // 수영 기구 사용 가능 여부 (킥판, 오리발, 패들 등)
    @Column(name = "equipment_available", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean equipmentAvailable = false;

    // 요금 특이사항 존재 여부 및 내용
    @Column(name = "price_note_flag", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean priceNoteFlag = false;

    @Column(name = "price_note", columnDefinition = "TEXT")
    private String priceNote;

    // 자유수영 시간표 특이사항 존재 여부 및 내용
    @Column(name = "timetable_note_flag", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean timetableNoteFlag = false;

    @Column(name = "timetable_note", columnDefinition = "TEXT")
    private String timetableNote;

    @Column(name = "website")
    private String website;

    @Column(name = "kakao_id", unique = true)
    private String kakaoId;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    private Boolean isDeleted = false;

    @Column(name = "admin_comment", columnDefinition = "TEXT")
    private String adminComment;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
