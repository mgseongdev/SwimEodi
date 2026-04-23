package com.swimeodi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PoolTimetableDto {
    private Long id;
    private String dayOfWeek;
    private Integer sessionNo;
    private String startTime;
    private String endTime;
}
