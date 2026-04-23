package com.swimeodi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PoolDto {
    private Long id;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private String phone;
    private String operatingHours;
    private String operatingHoursNote;
    private String price;
    private String description;
    private String locationType;
    private Integer laneLength;
    private Integer laneCount;
    private Boolean hasBabyPool;
    private String showerCleanliness;
    private Boolean hasTowelService;
    private Boolean hasHairDryer;
    private String hairDryerFee;
    private Boolean parkingAvailable;
    private String parkingFee;
    private String depth;
    private Integer capacity;
    private Boolean freeSwimmingAvailable;
    private String poolType;
    private String facilityType;
    private Boolean equipmentAvailable;
    private Boolean priceNoteFlag;
    private String priceNote;
    private Boolean timetableNoteFlag;
    private String timetableNote;
    private String website;
    private String adminComment;
    private List<PoolTimetableDto> timetable;
    private List<PoolPriceDto> prices;
}
