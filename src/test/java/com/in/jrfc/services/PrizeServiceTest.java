package com.in.jrfc.services;

import com.in.jrfc.entities.Prize;
import com.in.jrfc.repositories.PrizesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PrizeServiceTest {


    private List<Prize> prizes;

//    @Test
//    void getCurrentPrizeByProductIdAndBrandId() {
//        while  q  }
//}    @Mock

    @Mock
    private PrizesRepository prizesRepository;


    @InjectMocks

    private PrizesService prizesService;


    private Prize prize;

    @BeforeEach
    void setUp() {

        Prize prize1 = Prize.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 15:00:000"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).prizeList(1)
                .productId(35455).priority(0).prize(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        Prize prize2 = Prize.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 15:00:00"))
                .endDate(Timestamp.valueOf("2020-06-14 18:30:00")).prizeList(2)
                .productId(35455).priority(1).prize(BigDecimal.valueOf(25.45))
                .curr("EUR").build();
        Prize prize3 = Prize.builder().brandId(1L)
                .startDate(Timestamp.valueOf("020-06-15 00:00:00"))
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
    }
    @Test
    public void getCurrentPrizeByProductIdAndBrandId(){

        // given - precondition or setup
        when(prizesRepository.findByProductIdAndBrandId(35455,1L)).thenReturn(prizes);
    }
}
