package com.swimeodi.repository;

import com.swimeodi.entity.PoolPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PoolPriceRepository extends JpaRepository<PoolPrice, Long> {
    List<PoolPrice> findByPoolId(Long poolId);
    List<PoolPrice> findByPoolIdIn(List<Long> poolIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM PoolPrice p WHERE p.pool.id = :poolId")
    void deleteByPoolId(Long poolId);
}
