package com.in.jrfc.entities;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
class PrizeTest {
    private Prize prize1;
    private Prize prize2;

    private final Date dateBefore = Timestamp.valueOf("2020-06-13 23:59:00");
    private final Date dateBetween = Timestamp.valueOf("2020-06-15 00:00:00");
    private final Date dateAfter = Timestamp.valueOf("2021-01-01 00:00:00");

    @BeforeEach
    void setUp() {
        this.prize1 = Prize.builder().id(1L).brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).prizeList(1)
                .productId(35455).priority(0).prize(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        this.prize2 = Prize.builder().id(1L).brandId(1L)
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
        assertEquals(prize1, prize2);
    }


    @Test
    void testHashCode() {
        log.info("===>>> hashCode:" + this.prize1.hashCode());
    }

    @Test
    @DisplayName("test_if_the_filter_date_is_between_the_prize_range_days")
    void validPrizeRangeTest() {


        //given a prize1 range dates Then expect true
        Assertions.assertTrue(this.prize1
                .validPrizeRange(dateBetween), "testDate= " + dateBetween
                + "prize1 startDate= " + this.prize1.getStartDate()
                + " and endDate= " + this.prize1.getEndDate());
    }

    @Test
    @DisplayName("test_if_the_filter_date_is_not_between_the_prize_range_days")
    void testInvalidPrizeRange() {
        //given a prize1 range dates then expect false
        Assertions.assertAll(
                () -> Assertions.assertFalse(this.prize1.validPrizeRange(dateBefore)
                        ,"testDate= " + dateBefore+ " prize1 startDate= " + this.prize1.getStartDate()),
                () -> Assertions.assertFalse(this.prize1.validPrizeRange(dateAfter)
                        , "testDate= " +dateAfter +" prize1 endDate= " + this.prize1.getEndDate()));


    }


    @Test
    @DisplayName("test_the_remaining_prize_application_days_list")
    void lookForApplicationDates() {
        Assertions.assertTrue(this.prize1.lookForApplicationDates(dateBetween).size()>1);
    }

    @Test
    void deleteInBatch() {
    }
}