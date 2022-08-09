package com.in.jrfc;

import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import com.in.jrfc.entities.Prize;
import com.in.jrfc.repositories.PrizesRepository;
import com.in.jrfc.services.PrizesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ExpectedBehaviorIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PrizesService prizesService;
    @MockBean
    private PrizesRepository prizesRepository;
    Prize prize;
    private final List<LocalDate> localDates = new ArrayList<>();
    private final List<Prize> prizes = new ArrayList<>();
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

    @ParameterizedTest
    @MethodSource("prizeRequestDtoProviderFactory")
    void testAllPrizes(PrizeRequestDto prizeRequestDtoAll) throws ExecutionException, InterruptedException {

        when(prizesRepository.findByProductIdAndBrandId(prizeRequestDtoAll.getProductId(), prizeRequestDtoAll.getBrandId())).thenReturn(prizes);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/prize/{hour},{productId},{brandId}", prizeRequestDtoAll.getRequestDate()
                                    , prizeRequestDtoAll.getProductId(), prizeRequestDtoAll.getBrandId()
                            )
                            .contentType("application/json")
            ).andDo(print()).andExpect(status().isOk());
            PrizeResponseDto responseDtoResult = prizesService.getCurrentPrizeByProductIdAndBrandId(prizeRequestDtoAll);
            Assertions.assertAll(
                    () -> assertEquals(35455, responseDtoResult.getProductId()
                            , "ProductIOd " + responseDtoResult.getProductId()),
                    () -> assertEquals(1L, responseDtoResult.getBrandId()
                            , "branddD " + responseDtoResult.getBrandId()),
                    () -> assertEquals(true, responseDtoResult.getApplicationDates().size() > 0
                            , "mandatory aplication days" + responseDtoResult.getApplicationDates().size()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    static Iterator<PrizeRequestDto> prizeRequestDtoProviderFactory() {
        List<PrizeRequestDto> requestDtos = new ArrayList<>();
        requestDtos.add(PrizeRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 10:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(PrizeRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 16:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(PrizeRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 21:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(PrizeRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-15 10:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(PrizeRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-16 21:00:00"))
                .productId(35455)
                .brandId(1L).build());
        return requestDtos.stream().iterator();
    }
    @Test
    void deleteInBatch() {
    }
}
