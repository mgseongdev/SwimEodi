package com.swimeodi.repository;

import com.swimeodi.entity.PoolTimetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PoolTimetableRepository extends JpaRepository<PoolTimetable, Long> {
    List<PoolTimetable> findByPoolIdOrderByDayOfWeekAscSessionNoAsc(Long poolId);
    List<PoolTimetable> findByPoolIdIn(List<Long> poolIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM PoolTimetable t WHERE t.pool.id = :poolId")
    void deleteByPoolId(Long poolId);
}
