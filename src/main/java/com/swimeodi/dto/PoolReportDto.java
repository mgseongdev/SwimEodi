package com.swimeodi.dto;

import com.swimeodi.entity.PoolReport.ReportStatus;
import com.swimeodi.entity.PoolReport.ReportType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PoolReportDto {
    private Long id;
    private ReportType reportType;
    private Long poolId;

    // 기본 정보
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private String phone;
    private String website;
    private String description;

    // 운영 정보
    private String operatingHours;
    private String operatingHoursNote;
    private Boolean freeSwimmingAvailable;
    private Boolean priceNoteFlag;
    private String priceNote;
    private Boolean timetableNoteFlag;
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
    private Boolean equipmentAvailable;

    // 편의시설
    private Boolean hasTowelService;
    private Boolean hasHairDryer;
    private String hairDryerFee;
    private Boolean parkingAvailable;
    private String parkingFee;
    private String showerCleanliness;

    // 시간표 & 요금
    private List<PoolTimetableDto> timetable;
    private List<PoolPriceDto> prices;

    // 제보 메타
    private String reporterNote;
    private ReportStatus status;
    private String adminComment;
    private LocalDateTime createdAt;
}
