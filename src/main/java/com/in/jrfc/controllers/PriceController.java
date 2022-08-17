package com.in.jrfc.controllers;

import com.in.jrfc.dtos.PriceRequestDto;
import com.in.jrfc.dtos.PriceResponseDto;
import com.in.jrfc.exceptions.PriceNotFoundException;
import com.in.jrfc.services.PriceAsyncService;
import com.in.jrfc.utility.PriceServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/price")
public class PriceController {

    @Autowired
    private final PriceAsyncService priceAsyncService;

    @Autowired
    public PriceController(PriceAsyncService priceAsyncService) {
        this.priceAsyncService = priceAsyncService;
    }

    @GetMapping(value = "/{hour},{productId},{brandId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceResponseDto> getPrice(@PathVariable (value = "hour")
                                                     @DateTimeFormat(pattern = PriceServiceUtils.FORMATO_FECHA) final Date hour,
                                                     @PathVariable (value="productId") Integer productId,
                                                     @PathVariable (value = "brandId") Long brandId) throws PriceNotFoundException,
            InterruptedException, ExecutionException {
        final PriceRequestDto priceFilterParams = new PriceRequestDto(hour, productId, brandId);
        PriceResponseDto respFuture = priceAsyncService.getCurrentPriceByProductIdAndBrandId(priceFilterParams).get();
        if (respFuture != null) {
            return new ResponseEntity<>(respFuture, HttpStatus.OK);
        } else {
            throw new PriceNotFoundException();
        }
    }



}
