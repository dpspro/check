package com.in.jrfc;

import com.in.jrfc.dtos.PriceRequestDto;
import com.in.jrfc.dtos.PriceResponseDto;
import com.in.jrfc.entities.Price;
import com.in.jrfc.repositories.PriceRepository;
import com.in.jrfc.services.PriceAsyncService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private PriceAsyncService priceService;
    @MockBean
    private PriceRepository priceRepository;
    Price price;
    private final List<LocalDate> localDates = new ArrayList<>();
    private final List<Price> priceList = new ArrayList<>();
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

        priceResponseDto = PriceResponseDto.builder()
                .productId(price3.getProductId())
                .brandId(price3.getBrandId())
                .priceList(price3.getPriceList())
                .applicationDates(new ArrayList<>())
                .price(price3.getPrice()).build();

        priceRequestDto = PriceRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 11:55:00"))
                .brandId(1L)
                .productId(35455).build();

    }

    @ParameterizedTest
    @MethodSource("priceRequestDtoProviderFactory")
    void testAllPrice(PriceRequestDto priceRequestDtoAll) {

        when(priceRepository.findByProductIdAndBrandId(priceRequestDtoAll.getProductId(), priceRequestDtoAll.getBrandId())).thenReturn(priceList);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/price/{hour},{productId},{brandId}", priceRequestDtoAll.getRequestDate()
                                    , priceRequestDtoAll.getProductId(), priceRequestDtoAll.getBrandId()
                            )
                            .contentType("application/json")
            ).andDo(print()).andExpect(status().isOk());
            PriceResponseDto responseDtoResult = priceService.getCurrentPriceByProductIdAndBrandId(priceRequestDtoAll).get();
            Assertions.assertAll(
                    () -> assertEquals(35455, responseDtoResult.getProductId()
                            , "ProductIOd " + responseDtoResult.getProductId()),
                    () -> assertEquals(1L, responseDtoResult.getBrandId()
                            , "brandId " + responseDtoResult.getBrandId()),
                    () -> assertTrue(responseDtoResult.getApplicationDates().size() > 0, "mandatory aplication days" + responseDtoResult.getApplicationDates().size()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    static Iterator<PriceRequestDto> priceRequestDtoProviderFactory() {
        List<PriceRequestDto> requestDtos = new ArrayList<>();
        requestDtos.add(PriceRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 10:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(PriceRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 16:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(PriceRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 21:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(PriceRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-15 10:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(PriceRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-16 21:00:00"))
                .productId(35455)
                .brandId(1L).build());
        return requestDtos.stream().iterator();
    }
    @Test
    void deleteInBatch() {
    }
}
