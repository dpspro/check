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
class PriceTest {
    private Price price1;
    private Price price2;

    private final Date dateBefore = Timestamp.valueOf("2020-06-13 23:59:00");
    private final Date dateBetween = Timestamp.valueOf("2020-06-15 00:00:00");
    private final Date dateAfter = Timestamp.valueOf("2021-01-01 00:00:00");

    @BeforeEach
    void setUp() {
        this.price1 = Price.builder().id(1L).brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).priceList(1)
                .productId(35455).priority(0).price(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        this.price2 = Price.builder().id(1L).brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).priceList(1)
                .productId(35455).priority(0).price(BigDecimal.valueOf(35.50))
                .curr("EUR").build();

        new Price();
    }

    @Test
    void testGetters() {
        log.info("===>>> getter:" + this.price1.getId());
        log.info("===>>> getter:" + this.price1.getBrandId());
        log.info("===>>> getter:" + this.price1.getStartDate());
        log.info("===>>> getter:" + this.price1.getEndDate());
        log.info("===>>> getter:" + this.price1.getPriceList());
        log.info("===>>> getter:" + this.price1.getProductId());
        log.info("===>>> getter:" + this.price1.getPriority());
        log.info("===>>> getter:" + this.price1.getPrice());
        log.info("===>>> getter:" + this.price1.getCurr());

    }

    @Test
    void testToString() {
        log.info("===>>> toString:" + this.price1.toString());
    }

    @Test
    void testEquals() {
        assertEquals(price1, price2);
    }


    @Test
    void testHashCode() {
        log.info("===>>> hashCode:" + this.price1.hashCode());
    }

    @Test
    @DisplayName("test_if_the_filter_date_is_between_the_price_range_days")
    void validPriceRangeTest() {


        //given a price1 range dates Then expect true
        Assertions.assertTrue(this.price1
                .validPriceRange(dateBetween), "testDate= " + dateBetween
                + "price1 startDate= " + this.price1.getStartDate()
                + " and endDate= " + this.price1.getEndDate());
    }

    @Test
    @DisplayName("test_if_the_filter_date_is_not_between_the_price_range_days")
    void testInvalidPriceRange() {
        //given a price1 range dates then expect false
        Assertions.assertAll(
                () -> Assertions.assertFalse(this.price1.validPriceRange(dateBefore),"testDate= " + dateBefore+ " price1 startDate= " + this.price1.getStartDate()),
                () -> Assertions.assertFalse(this.price1.validPriceRange(dateAfter), "testDate= " +dateAfter +" price1 endDate= " + this.price1.getEndDate()));


    }


    @Test
    @DisplayName("test_the_remaining_price_application_days_list")
    void lookForApplicationDates() {
        Assertions.assertTrue(this.price1.lookForApplicationDates(dateBetween).size()>1);
    }

    @Test
    void deleteInBatch() {
    }
}