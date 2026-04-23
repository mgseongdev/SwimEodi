package com.swimeodi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pool_prices")
@Getter
@Setter
@NoArgsConstructor
public class PoolPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id", nullable = false)
    private Pool pool;

    @Column(name = "pool_id", insertable = false, updatable = false)
    private Long poolId;

    // 이용권 종류 (예: 자유수영 일일입장, 자유수영 정기권, 수영 강습 등 — 자유 텍스트)
    @Column(name = "ticket_type", nullable = false)
    private String ticketType;

    // 요일 구분 — NULL이면 구분 없음
    @Column(name = "day_type")
    private String dayType;

    // 연령 구분 — NULL이면 구분 없음
    @Column(name = "age_group")
    private String ageGroup;

    // 금액 (원 단위)
    private Integer price;

    // 기타 메모
    private String note;
}
