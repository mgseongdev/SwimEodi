package com.swimeodi.repository;

import com.swimeodi.entity.Pool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PoolRepository extends JpaRepository<Pool, Long> {
    List<Pool> findByIsDeletedFalse();
    List<Pool> findByIsDeletedFalseAndNameContainingIgnoreCase(String name);
    List<Pool> findByIsDeletedFalseAndAddressContainingIgnoreCase(String address);
    Optional<Pool> findByIdAndIsDeletedFalse(Long id);
    boolean existsByKakaoId(String kakaoId);
}
