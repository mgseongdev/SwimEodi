package com.swimeodi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pool_timetable")
@Getter
@Setter
@NoArgsConstructor
public class PoolTimetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id", nullable = false)
    private Pool pool;

    @Column(name = "pool_id", insertable = false, updatable = false)
    private Long poolId;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    @Column(name = "session_no", nullable = false)
    private Integer sessionNo;

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time", nullable = false)
    private String endTime;
}
