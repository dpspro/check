package com.in.jrfc.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrizeResponseDto {
    private Integer productId;
    private Long brandId;
    private Integer prizeList;
    private List<LocalDate> applicationDates;
    private BigDecimal prize;
}