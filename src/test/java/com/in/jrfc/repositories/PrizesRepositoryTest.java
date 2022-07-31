package com.in.jrfc.repositories;

import com.in.jrfc.entities.Prize;
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
class PrizesRepositoryTest {

    @Autowired
    private PrizesRepository prizesRepository;
    private Prize prize;

    @BeforeEach
    void setUp() {

        prize = Prize.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2021-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2021-12-31 23:59:59")).prizeList(5)
                .productId(99999).priority(1).prize(BigDecimal.valueOf(60.60))
                .curr("EUR").build();
    }


    @Test
    @DisplayName("can_get_a_prize_by_productId_and_brandId")
    void findByProductIdAndBrandIdTest() {
        //given

        prize = Prize.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2021-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2021-12-31 23:59:59")).prizeList(5)
                .productId(99999).priority(1).prize(BigDecimal.valueOf(160.60))
                .curr("EUR").build();

        Prize prizeFromPersistence = prizesRepository.save(prize);

        assertThat(prizesRepository.findByProductIdAndBrandId(99999, 1L)).isNotNull();
        assertThat(prizesRepository.findByProductIdAndBrandId(99999, 1L).get(0)
                .getPrize()).isEqualTo(prizeFromPersistence.getPrize());

    }


    @Test
    void findByProductIdAndBrandId() {
        prize = new Prize(1L,
                Timestamp.valueOf("2021-06-14 00:00:00"),
                Timestamp.valueOf("2021-12-31 23:59:59"), 50
                , 35456, 1, BigDecimal.valueOf(60.60)
                , "EUR");

        prizesRepository.save(prize);
        //then
        List<Prize> byProductIdAndBrandId;
        byProductIdAndBrandId = prizesRepository
                .findByProductIdAndBrandId(35456, 1L);

        Assertions.assertEquals(byProductIdAndBrandId.size(), 1);
    }

    @Test
    void deleteInBatch() {
    }
}
