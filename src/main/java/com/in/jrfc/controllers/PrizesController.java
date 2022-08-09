package com.in.jrfc.controllers;

import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import com.in.jrfc.exceptions.PrizeNotFoundException;
import com.in.jrfc.services.AsynchronousService;
import com.in.jrfc.utility.PrizeServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.Future;


@RestController
@RequestMapping("/prize")
public class PrizesController {

    private final static Logger LOGGER = LoggerFactory.getLogger(PrizesController.class);
    @Autowired
    private final AsynchronousService asynchronousService;

    @Autowired
    public PrizesController(AsynchronousService asynchronousService) {
        this.asynchronousService = asynchronousService;
    }

    @GetMapping(value = "/{hour},{productId},{brandId}", produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<PrizeResponseDto> getPrize(@PathVariable (value = "hour")
                                                     @DateTimeFormat(pattern = PrizeServiceUtils.FORMATO_FECHA) final Date hour,
                                                     @PathVariable (value="productId") Integer productId,
                                                     @PathVariable (value = "brandId") Long brandId) throws PrizeNotFoundException, InterruptedException, ExecutionException {
        final PrizeRequestDto prizeFilterParams = new PrizeRequestDto(hour, productId, brandId);
        Future<PrizeResponseDto> respFuture = asynchronousService.asyncPrizeResponse(prizeFilterParams);
        if (respFuture != null){
            return new ResponseEntity<PrizeResponseDto>(respFuture.get(), HttpStatus.OK);
    }else{
            throw new PrizeNotFoundException();
    }
    }




}
