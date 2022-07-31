package com.in.jrfc.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrizeRequestDto {
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    // Allows dd/MM/yyyy date to be passed into GET request in JSON
    @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    private Date requestDate;
    private Integer productId;
    private Long brandId;
}