package com.in.jrfc.services;


import com.in.jrfc.dtos.PriceRequestDto;
import com.in.jrfc.dtos.PriceResponseDto;
import com.in.jrfc.entities.Price;
import com.in.jrfc.repositories.PriceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class PriceAsyncServiceTest {
    private final List<LocalDate> localDates = new ArrayList<>();
    @Autowired
    private ThreadPoolTaskExecutor myTaskExecutor;
    @Mock
    private PriceAsyncService priceAsyncService;
    private final List<Price> priceList = new ArrayList<>();

    @Mock
    private PriceRepository priceRepository;
    private Price price;
    private PriceResponseDto priceResponseDto;
    private PriceRequestDto priceRequestDto;


    @BeforeEach
    void setUp() {

        Price price1 = Price.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).priceList(1)
                .productId(35455).priority(0).price(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        Price price2 = Price.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 15:00:00"))
                .endDate(Timestamp.valueOf("2020-06-14 18:30:00")).priceList(2)
                .productId(35455).priority(1).price(BigDecimal.valueOf(25.45))
                .curr("EUR").build();
        Price price3 = Price.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-15 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-15 11:00:00")).priceList(3)
                .productId(35455).priority(1).price(BigDecimal.valueOf(30.50))
                .curr("EUR").build();
        Price price4 = Price.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-15 16:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).priceList(4)
                .productId(35455).priority(1).price(BigDecimal.valueOf(38.95))
                .curr("EUR").build();
        priceList.add(price1);
        priceList.add(price2);
        priceList.add(price3);
        priceList.add(price4);
        price = price2;

        priceResponseDto = PriceResponseDto.builder()//PriceResponseDto.PriceResponseDtoBuilder builder = PriceResponseDto.builder();
        .productId(price3.getProductId())
        .brandId(price3.getBrandId())
        .priceList(price3.getPriceList())
        .applicationDates(localDates)
        .price(price3.getPrice()).build();



        priceRequestDto = PriceRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 11:55:00"))
                .brandId(1L)
                .productId(35455).build();
    }


    @Test
    @DisplayName("look_for_all_price_by_productIid_and_brandId")
    public void testGetCurrentPriceByProductIdAndBrandId() {

        when(priceRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(this.priceList);


        List<Price> priceList = this.priceRepository.findByProductIdAndBrandId(35455, 1L);

        assertNotNull(priceList, "priceList object should not be null");
        assertEquals(4, priceList.size(), "priceList size should  be 4");


    }


    @Test
    void testgetPrice() {
        when(priceRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(priceList);
       when(this.priceAsyncService.getPrice(priceRequestDto, priceList)).thenReturn(this.price);
       Price price1=priceAsyncService.getPrice(priceRequestDto,priceRepository.findByProductIdAndBrandId(35455, 1L));
        assertNotNull(price1, "price object should not be null");
        assertAll("getPrice",
                () -> assertEquals(BigDecimal.valueOf(25.45), price.getPrice()),
                () -> assertEquals(35455, price.getProductId()));
    }


    @Test
    void testentityToDto() {
        LocalDate localDate= LocalDate.ofInstant(priceRequestDto.getRequestDate().toInstant(), ZoneId.of("UTC"));
        localDates.add(localDate);
//        PriceResponseDto   priceResponseDto1 = PriceResponseDto.builder()//PriceResponseDto.PriceResponseDtoBuilder builder = PriceResponseDto.builder();
//                .productId(price.getProductId())
//                .brandId(1L)
//                .priceList(2)
//                .applicationDates(localDates)
//                .price(BigDecimal.valueOf(30.5)).build();
      //  when(priceRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(price);
        when(this.priceAsyncService.entityToDto(this.price, priceRequestDto.getRequestDate())).thenReturn(this.priceResponseDto);
       this.priceResponseDto = priceAsyncService.entityToDto(this.price, priceRequestDto.getRequestDate());
        assertNotNull(priceResponseDto, "priceResponseDto object should not be null");
        final Runnable runnable = () -> assertEquals(1, priceResponseDto.getApplicationDates().size());
        assertAll(
                () -> assertEquals(BigDecimal.valueOf(30.5), priceResponseDto.getPrice()),
                () -> assertEquals(1, priceResponseDto.getApplicationDates().size())
        );
    }

    @Test
    void asyncPriceResponse() {


        PriceRequestDto priceRequestDto = new PriceRequestDto(Timestamp.valueOf("2020-06-14 00:00:00"), 1, 1L);
        PriceResponseDto priceResponseDto = new PriceResponseDto(35455, 1L, 1, localDates, BigDecimal.valueOf(35.50));
        when(priceAsyncService.getCurrentPriceByProductIdAndBrandId(priceRequestDto))
                .thenReturn(new AsyncResult<>(priceResponseDto));
        this.priceAsyncService.getCurrentPriceByProductIdAndBrandId(priceRequestDto);

        try {
            boolean awaitTermination = this.myTaskExecutor.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);
            Assertions.assertFalse(awaitTermination);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    @DisplayName("time_sleep_test")
    public void testSleepTime() throws ExecutionException, InterruptedException {
        PriceRequestDto priceRequestDto = new PriceRequestDto(Timestamp.valueOf("2020-06-14 00:00:00"), 1, 1L);
        PriceResponseDto priceResponseDto = new PriceResponseDto(35455, 1L, 1, localDates, BigDecimal.valueOf(35.50));
       when(this.priceAsyncService.getCurrentPriceByProductIdAndBrandId(priceRequestDto))
                .thenReturn(new AsyncResult<>(priceResponseDto));

        long now = System.currentTimeMillis();

        // <2>  Blocking waiting for results


        Future<PriceResponseDto> executeResult = this.priceAsyncService.getCurrentPriceByProductIdAndBrandId(priceRequestDto);

        // <1>  Perform tasks
         long sleep = 1000;
        executeResult.get();


        assertTrue((System.currentTimeMillis()+sleep-now  ) >= 1000);


    }
}