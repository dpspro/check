package com.in.jrfc.repositories;

import com.in.jrfc.entities.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrizesRepository extends JpaRepository<Prize, Long> {
    List<Prize> findByProductIdAndBrandId(Integer productId,
                                          Long brandId);
}