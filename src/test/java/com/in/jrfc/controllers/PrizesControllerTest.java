package com.in.jrfc.controllers;

import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import com.in.jrfc.entities.Prize;
import com.in.jrfc.services.AsynchronousService;
import com.in.jrfc.services.PrizesAsyncService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@RunWith(SpringRunner.class)

@WebMvcTest(PrizesController.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PrizesControllerTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(PrizesControllerTest.class);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private Prize prize;
    private List<LocalDate> prizes;
    private PrizeRequestDto prizeRequestDto;
    private PrizeResponseDto prizeResponseDto;

    @MockBean
    private PrizesAsyncService prizesAsyncService;
    @MockBean
    private AsynchronousService asynchronousService;
    @MockBean
    private PrizesController prizesController;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        prizesAsyncService = mock(PrizesAsyncService.class);
        this.prizesController = new PrizesController(prizesAsyncService);
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
        when(prizesAsyncService.getCurrentPrizeByProductIdAndBrandId(prizeRequestDto))
                .thenReturn(new AsyncResult<>(this.prizeResponseDto));
        mockMvc.perform(get("/prize/{hour},{productId},{brandId}", hour, pId, bId).contentType("application/json")
                ).andDo(print()).
                andExpect(status().isOk());
        Future<PrizeResponseDto> responseDtoResult = prizesAsyncService.getCurrentPrizeByProductIdAndBrandId(prizeRequestDto);

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
        this.prize = null;
        when(prizesAsyncService.getCurrentPrizeByProductIdAndBrandId(null))
                .thenReturn(new AsyncResult<>(null));
        mockMvc.perform(get("/prize/{hour},{productId},{brandId}", hour, pId, bId)
                .contentType("application/json")
            ).andDo(print()).andExpect(status().isBadRequest());
        Future<PrizeResponseDto> responseDtoResult = prizesAsyncService.getCurrentPrizeByProductIdAndBrandId(prizeRequestDto);
        Assertions.assertNull(responseDtoResult);


    }

    @Test
    void deleteInBatch() {

    }


}