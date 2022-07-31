package com.in.jrfc.entities;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Reference;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
class PrizeTest {
    private Prize prize1;
    private Prize prize2;

    @BeforeEach
    void setUp() {
        this.prize1 = Prize.builder().id(null).brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).prizeList(1)
                .productId(35455).priority(0).prize(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        this.prize2 = Prize.builder().id(null).brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).prizeList(1)
                .productId(35455).priority(0).prize(BigDecimal.valueOf(35.50))
                .curr("EUR").build();

        new Prize();
    }

    @Test
    void testGetters() {
        log.info("===>>> getter:" + this.prize1.getId());
        log.info("===>>> getter:" + this.prize1.getBrandId());
        log.info("===>>> getter:" + this.prize1.getStartDate());
        log.info("===>>> getter:" + this.prize1.getEndDate());
        log.info("===>>> getter:" + this.prize1.getPrizeList());
        log.info("===>>> getter:" + this.prize1.getProductId());
        log.info("===>>> getter:" + this.prize1.getPriority());
        log.info("===>>> getter:" + this.prize1.getPrize());
        log.info("===>>> getter:" + this.prize1.getCurr());

    }

    @Test
    void testToString() {
        log.info("===>>> toString:" + this.prize1.toString());
    }

    @Test
    void testEquals() {
        assertEquals(this.prize1, this.prize2);
    }


    @Test
    void testHashCode() {
        log.info("===>>> hashCode:" + this.prize1.hashCode());
    }

    @Test
    @DisplayName("test_if_the_filter_date_is_between_the_prize_range_days")
    void validPrizeRangeTest() {
        //given two dates
        Date dateBetween = Timestamp.valueOf("2020-06-15 00:00:00");
        Date dateBefore = Timestamp.valueOf("2020-06-13 23:59:00");
        Date dateAfter = Timestamp.valueOf("2021-01-01 00:00:00");

        // Then expect true
        Assertions.assertTrue(this.prize1.validPrizeRange(dateBetween));

        // And expect false

        Assertions.assertFalse(this.prize1.validPrizeRange(dateBefore));
        Assertions.assertFalse(this.prize1.validPrizeRange(dateAfter));

    }

}