package com.in.jrfc.controllers;

import com.in.jrfc.dtos.PriceRequestDto;
import com.in.jrfc.dtos.PriceResponseDto;
import com.in.jrfc.entities.Price;
import com.in.jrfc.services.PriceAsyncService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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

@WebMvcTest(PriceController.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private Price price;
    private List<LocalDate> priceList;
    private PriceRequestDto priceRequestDto;

    @MockBean
    private PriceAsyncService priceAsyncService;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        priceAsyncService = mock(PriceAsyncService.class);
        PriceController priceController = new PriceController(priceAsyncService);
        this.priceRequestDto = PriceRequestDto.builder().requestDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .productId(35455).brandId(1L).build();
        this.priceList = new ArrayList<>();
        this.priceList.add(LocalDate.ofInstant(this.priceRequestDto.getRequestDate().toInstant(), ZoneId.of("UTC")));


    }

    @Test
    void getPriceAsync() throws Exception {
        this.price = Price.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).priceList(1)
                .productId(35455).priority(0).price(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        PriceResponseDto priceResponseDto = PriceResponseDto.builder()
                .productId(price.getProductId())
                .brandId(price.getBrandId())
                .priceList(price.getPriceList())
                .applicationDates(priceList)
                .price(price.getPrice()).build();
        String hour = "2020-06-14 00:00:00";
        Integer pId = 35455;
        String bId = "1";
        when(priceAsyncService.getCurrentPriceByProductIdAndBrandId(priceRequestDto))
                .thenReturn(new AsyncResult<>(priceResponseDto));
        mockMvc.perform(get("/price/{hour},{productId},{brandId}", hour, pId, bId).contentType("application/json")
                ).andDo(print()).
                andExpect(status().isOk());
        Future<PriceResponseDto> responseDtoResult = priceAsyncService.getCurrentPriceByProductIdAndBrandId(priceRequestDto);

        Assertions.assertEquals(responseDtoResult.get().getPrice(), BigDecimal.valueOf(35.50));
        Assertions.assertEquals(responseDtoResult.get().getProductId(), 35455);
        Assertions.assertEquals(responseDtoResult.get().getBrandId(), 1L);
        Assertions.assertEquals(responseDtoResult.get().getApplicationDates().size(), 1);

    }

    @Test
    void getPriceAsyncBadRequest() throws Exception {
        String hour = "2020-06_14 00:00:00";
        Integer pId = 15;
        String bId = "1";
        this.price = null;
        when(priceAsyncService.getCurrentPriceByProductIdAndBrandId(null))
                .thenReturn(new AsyncResult<>(null));
        mockMvc.perform(get("/price/{hour},{productId},{brandId}", hour, pId, bId)
                .contentType("application/json")
            ).andDo(print()).andExpect(status().isBadRequest());
        Future<PriceResponseDto> responseDtoResult = priceAsyncService.getCurrentPriceByProductIdAndBrandId(priceRequestDto);
        Assertions.assertNull(responseDtoResult);


    }

    @Test
    void deleteInBatch() {

    }


}