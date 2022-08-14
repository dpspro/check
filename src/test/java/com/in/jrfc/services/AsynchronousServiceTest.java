package com.in.jrfc.services;

import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
@Disabled
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AsynchronousServiceTest {
    private final List<LocalDate> localDates = new ArrayList<>();


    @Autowired
    private ThreadPoolTaskExecutor myTaskExecutor;
    @Mock
    private AsynchronousService asynchronousService;

    @BeforeEach
    void setUp() {



    }

//    @Test
//    void asyncPrizeResponse() {
//
//
//        PrizeRequestDto prizeRequestDto = new PrizeRequestDto(Timestamp.valueOf("2020-06-14 00:00:00"), 1, 1L);
//        PrizeResponseDto prizeResponseDto = new PrizeResponseDto(35455, 1L, 1, localDates, BigDecimal.valueOf(35.50));
//        when(asynchronousService.asyncPrizeResponse(prizeRequestDto))
//                .thenReturn(new AsyncResult<>( prizeResponseDto));
//        this.asynchronousService.asyncPrizeResponse(prizeRequestDto);
//
//        try {
//            boolean awaitTermination = this.myTaskExecutor.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);
//            Assertions.assertFalse(awaitTermination);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }
    void asyncPrizeResponse() {


        PrizeRequestDto prizeRequestDto = new PrizeRequestDto(Timestamp.valueOf("2020-06-14 00:00:00"), 1, 1L);
        PrizeResponseDto prizeResponseDto = new PrizeResponseDto(35455, 1L, 1, localDates, BigDecimal.valueOf(35.50));
        when(asynchronousService.asyncPrizeResponse(prizeRequestDto))
                .thenReturn(new AsyncResult<>( prizeResponseDto));
        this.asynchronousService.asyncPrizeResponse(prizeRequestDto);

        try {
            boolean awaitTermination = this.myTaskExecutor.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);
            Assertions.assertFalse(awaitTermination);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
    @Test
    void deleteInBatch() {
    }

}