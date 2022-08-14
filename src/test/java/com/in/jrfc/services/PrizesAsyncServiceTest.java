package com.in.jrfc.services;


import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import com.in.jrfc.entities.Prize;
import com.in.jrfc.repositories.PrizesRepository;
import com.in.jrfc.utility.PrizeServiceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class PrizesAsyncServiceTest {
    private final List<LocalDate> localDates = new ArrayList<>();
    @Autowired
    private ThreadPoolTaskExecutor myTaskExecutor;
    @Mock
    private PrizesAsyncService prizesAsyncService;
    private final List<Prize> prizes = new ArrayList<>();

    @Mock
    private PrizesRepository prizesRepository;
    private Prize prize;
    private PrizeResponseDto prizeResponseDto;
    private PrizeRequestDto prizeRequestDto;


    @BeforeEach
    void setUp() {

        Prize prize1 = Prize.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).prizeList(1)
                .productId(35455).priority(0).prize(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        Prize prize2 = Prize.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 15:00:00"))
                .endDate(Timestamp.valueOf("2020-06-14 18:30:00")).prizeList(2)
                .productId(35455).priority(1).prize(BigDecimal.valueOf(25.45))
                .curr("EUR").build();
        Prize prize3 = Prize.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-15 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-15 11:00:00")).prizeList(3)
                .productId(35455).priority(1).prize(BigDecimal.valueOf(30.50))
                .curr("EUR").build();
        Prize prize4 = Prize.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-15 16:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).prizeList(4)
                .productId(35455).priority(1).prize(BigDecimal.valueOf(38.95))
                .curr("EUR").build();
        prizes.add(prize1);
        prizes.add(prize2);
        prizes.add(prize3);
        prizes.add(prize4);
        prize = prize2;

        prizeResponseDto = PrizeResponseDto.builder()//PrizeResponseDto.PrizeResponseDtoBuilder builder = PrizeResponseDto.builder();
        .productId(prize3.getProductId())
        .brandId(prize3.getBrandId())
        .prizeList(prize3.getPrizeList())
        .applicationDates(localDates)
        .prize(prize3.getPrize()).build();



        prizeRequestDto = PrizeRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 11:55:00"))
                .brandId(1L)
                .productId(35455).build();
    }


    @Test
    @DisplayName("look_for_all_prizes_by_productIid_and_brandId")
    public void testGetCurrentPrizeByProductIdAndBrandId() {

        when(prizesRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(this.prizes);


        List<Prize> prizeList = this.prizesRepository.findByProductIdAndBrandId(35455, 1L);

        assertNotNull(prizeList, "prizeList object should not be null");
        assertEquals(4, prizeList.size(), "prizeList size should  be 4");


    }


    @Test
    void testgetPrize() {
        when(prizesRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(prizes);
       when(this.prizesAsyncService.getPrize(prizeRequestDto, prizes)).thenReturn(this.prize);
       Prize prize1=prizesAsyncService.getPrize(prizeRequestDto,prizesRepository.findByProductIdAndBrandId(35455, 1L));
        assertNotNull(prize1, "prize object should not be null");
        assertAll("getPrize",
                () -> assertEquals(BigDecimal.valueOf(25.45), prize.getPrize()),
                () -> assertEquals(35455, prize.getProductId()));
    }


    @Test
    void testentityToDto() {
        LocalDate localDate= LocalDate.ofInstant(prizeRequestDto.getRequestDate().toInstant(), ZoneId.of("UTC"));
        localDates.add(localDate);
//        PrizeResponseDto   prizeResponseDto1 = PrizeResponseDto.builder()//PrizeResponseDto.PrizeResponseDtoBuilder builder = PrizeResponseDto.builder();
//                .productId(prize.getProductId())
//                .brandId(1L)
//                .prizeList(2)
//                .applicationDates(localDates)
//                .prize(BigDecimal.valueOf(30.5)).build();
      //  when(prizesRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(prizes);
        when(this.prizesAsyncService.entityToDto(this.prize, prizeRequestDto.getRequestDate())).thenReturn(this.prizeResponseDto);
       this.prizeResponseDto = prizesAsyncService.entityToDto(this.prize, prizeRequestDto.getRequestDate());
        assertNotNull(prizeResponseDto, "prizeResponseDto object should not be null");
        final Runnable runnable = () -> assertEquals(1, prizeResponseDto.getApplicationDates().size());
        assertAll(
                () -> assertEquals(BigDecimal.valueOf(30.5), prizeResponseDto.getPrize()),
                () -> assertEquals(1, prizeResponseDto.getApplicationDates().size())
        );
    }

    @Test
    void asyncPrizeResponse() {


        PrizeRequestDto prizeRequestDto = new PrizeRequestDto(Timestamp.valueOf("2020-06-14 00:00:00"), 1, 1L);
        PrizeResponseDto prizeResponseDto = new PrizeResponseDto(35455, 1L, 1, localDates, BigDecimal.valueOf(35.50));
        when(prizesAsyncService.getCurrentPrizeByProductIdAndBrandId(prizeRequestDto))
                .thenReturn(new AsyncResult<>(prizeResponseDto));
        this.prizesAsyncService.getCurrentPrizeByProductIdAndBrandId(prizeRequestDto);

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
        PrizeRequestDto prizeRequestDto = new PrizeRequestDto(Timestamp.valueOf("2020-06-14 00:00:00"), 1, 1L);
        PrizeResponseDto prizeResponseDto = new PrizeResponseDto(35455, 1L, 1, localDates, BigDecimal.valueOf(35.50));
       when(this.prizesAsyncService.getCurrentPrizeByProductIdAndBrandId(prizeRequestDto))
                .thenReturn(new AsyncResult<>(prizeResponseDto));

        long now = System.currentTimeMillis();

        // <2>  Blocking waiting for results


        CountDownLatch latch = new CountDownLatch(1);
        // <1>  Perform tasks
        Future<PrizeResponseDto> executeResult = this.prizesAsyncService.getCurrentPrizeByProductIdAndBrandId(prizeRequestDto);
        long sleep = 1000;
        executeResult.get();


        assertTrue((System.currentTimeMillis()+sleep-now  ) >= 1000);


    }
}