package com.in.jrfc.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class PrizeResponseDto {
    private Integer productId;
    private Long brandId;
    private Integer prizeList;
    private List<LocalDate> applicationDates;
    private BigDecimal prize;


}