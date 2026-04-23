package com.swimeodi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PoolPriceDto {
    private Long id;
    private String ticketType;
    private String dayType;
    private String ageGroup;
    private Integer price;
    private String note;
}
