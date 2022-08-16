package com.in.jrfc.repositories;

import com.in.jrfc.entities.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findByProductIdAndBrandId(Integer productId,
                                          Long brandId);
}