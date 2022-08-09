package com.in.jrfc.services;

import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import com.in.jrfc.entities.Prize;
import com.in.jrfc.repositories.PrizesRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
@Log4j2
class PrizesServiceTest {


    @Mock
    private PrizesRepository prizesRepository;
    @Autowired
    private PrizesService prizesService;
    private Prize prize;
    private PrizeResponseDto prizeResponseDto;
    private PrizeRequestDto prizeRequestDto;
    private final List<Prize> prizes = new ArrayList<>();

    @BeforeEach
    void setUp() {

//        prize = Prize.builder().brandId(1L)
//                .startDate(Timestamp.valueOf((new Loc null))
//                .endDate(Timestamp.valueOf((LocalDateTime) null)).prizeList(1)
//                .productId(35455).priority(0).prize(BigDecimal.valueOf(35.50))
//                .curr("EUR").build();
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
        prizes.add(prize= Prize.builder().startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).prizeList(1)
                .productId(35455).priority(0).prize(BigDecimal.valueOf(35.50)).build());
//        prizes.add(prize1);
        prizes.add(prize2);
        prizes.add(prize3);
        prizes.add(prize4);
        prize = prize2;

        prizeResponseDto = PrizeResponseDto.builder()
                .productId(prize3.getProductId())
                .brandId(prize3.getBrandId())
                .prizeList(prize3.getPrizeList())
                .applicationDates(new ArrayList<LocalDate>())
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
        prize = this.prizesService.getPrize(prizeRequestDto, prizes);

        assertNotNull(prize, "prize object should not be null");
        assertAll("getPrize",
                () -> assertEquals(BigDecimal.valueOf(25.45), prize.getPrize()),
                () -> assertEquals(35455, prize.getProductId()));
    }


    @Test
    void testentityToDto() {

        prizeResponseDto = prizesService.entityToDto(prize, prizeRequestDto.getRequestDate());
        assertNotNull(prizeResponseDto, "prizeResponseDto object should not be null");
        final Runnable runnable = () -> assertEquals(1, prizeResponseDto.getApplicationDates().size());
        assertAll(
                () -> assertEquals(BigDecimal.valueOf(25.45), prizeResponseDto.getPrize()),
                () -> assertEquals(1, prizeResponseDto.getApplicationDates().size())
        );
    }

    @Test
    void deleteInBatch() {
    }
}
