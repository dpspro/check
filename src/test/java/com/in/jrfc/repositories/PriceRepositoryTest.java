package com.in.jrfc.repositories;

import com.in.jrfc.entities.Price;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class PriceRepositoryTest {

    @Autowired
    private PriceRepository priceRepository;
    private Price price;

    @BeforeEach
    void setUp() {

        price = Price.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2021-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2021-12-31 23:59:59")).priceList(5)
                .productId(99999).priority(1).price(BigDecimal.valueOf(60.60))
                .curr("EUR").build();
    }


    @Test
    @DisplayName("can_get_a_price_by_productId_and_brandId")
    void findByProductIdAndBrandIdTest() {
        //given

        price = Price.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2021-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2021-12-31 23:59:59")).priceList(5)
                .productId(99999).priority(1).price(BigDecimal.valueOf(160.60))
                .curr("EUR").build();

        Price priceFromPersistence = priceRepository.save(price);

        assertThat(priceRepository.findByProductIdAndBrandId(99999, 1L)).isNotNull();
        assertThat(priceRepository.findByProductIdAndBrandId(99999, 1L).get(0)
                .getPrice()).isEqualTo(priceFromPersistence.getPrice());

    }


    @Test
    void findByProductIdAndBrandId() {
        price = new Price(1L,
                Timestamp.valueOf("2021-06-14 00:00:00"),
                Timestamp.valueOf("2021-12-31 23:59:59"), 50
                , 35456, 1, BigDecimal.valueOf(60.60)
                , "EUR");

        priceRepository.save(price);
        //then
        List<Price> byProductIdAndBrandId;
        byProductIdAndBrandId = priceRepository
                .findByProductIdAndBrandId(35456, 1L);

        Assertions.assertEquals(byProductIdAndBrandId.size(), 1);
    }

    @Test
    void deleteInBatch() {
    }
}
