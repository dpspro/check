package com.in.jrfc.controllers;

import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import com.in.jrfc.entities.Prize;
import com.in.jrfc.services.AsynchronousService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

;


//@RunWith(SpringRunner.class)

@WebMvcTest(PrizesController.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PrizesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private Prize prize;
    private List<LocalDate> prizes;
    private PrizeRequestDto prizeRequestDto;
    private PrizeResponseDto prizeResponseDto;

    @MockBean
    private AsynchronousService asynchronousService;
    @MockBean
    private PrizesController prizesController;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        asynchronousService = mock(AsynchronousService.class);
        this.prizesController = new PrizesController(asynchronousService);
        this.prizeRequestDto = PrizeRequestDto.builder().requestDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .productId(35455).brandId(1L).build();
        this.prizes = new ArrayList<>();
        this.prizes.add(LocalDate.ofInstant(this.prizeRequestDto.getRequestDate().toInstant(), ZoneId.of("UTC")));


    }

    @Test
    void getPrizeAsync() throws Exception {
        this.prize = Prize.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).prizeList(1)
                .productId(35455).priority(0).prize(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        this.prizeResponseDto = PrizeResponseDto.builder()
                .productId(prize.getProductId())
                .brandId(prize.getBrandId())
                .prizeList(prize.getPrizeList())
                .applicationDates(prizes)
                .prize(prize.getPrize()).build();
        String hour = "2020-06-14 00:00:00";
        Integer pId = 35455;
        String bId = "1";
        when(asynchronousService.asyncPrizeResponse(prizeRequestDto))
                .thenReturn(new AsyncResult<>(this.prizeResponseDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/prize/{hour},{productId},{brandId}", hour, pId, bId))
                .andExpect(status().isOk());
        Future<PrizeResponseDto> responseDtoResult = asynchronousService.asyncPrizeResponse(prizeRequestDto);
        Assertions.assertEquals(responseDtoResult.get().getPrize(), BigDecimal.valueOf(35.50));
        Assertions.assertEquals(responseDtoResult.get().getProductId(), 35455);
        Assertions.assertEquals(responseDtoResult.get().getBrandId(), 1L);
        Assertions.assertEquals(responseDtoResult.get().getApplicationDates().size(), 1);

    }

    @Test
    void getPrizeAsyncBadRequest() throws Exception {
        String hour = "2020-06_14 00:00:00";
        Integer pId = 15;
        String bId = "1";
        this.prize=null;
        when(asynchronousService.asyncPrizeResponse(null))
                .thenReturn(new AsyncResult<>(null));
        mockMvc.perform(MockMvcRequestBuilders.get("/prize/{hour},{productId},{brandId}", hour, pId, bId))
                .andExpect(status().isBadRequest());
        Future<PrizeResponseDto> responseDtoResult = asynchronousService.asyncPrizeResponse(prizeRequestDto);
        Assertions.assertNull(responseDtoResult);


    }


    @Test
    void deleteInBatch() {

    }


}