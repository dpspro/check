package com.in.jrfc.controllers;

import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import com.in.jrfc.exceptions.PrizeNotFoundException;
import com.in.jrfc.services.AsynchronousService;
import com.in.jrfc.services.PrizesAsyncService;
import com.in.jrfc.services.PrizesService;
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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.*;


@RestController
@RequestMapping("/prizesas")
public class PrizesAsController {

    private final static Logger LOGGER = LoggerFactory.getLogger(PrizesAsController.class);
    @Autowired
    private final AsynchronousService asynchronousService;
  @Autowired
    private final PrizesAsyncService  prizesService;

    @Autowired
    public PrizesAsController(AsynchronousService asynchronousService, PrizesAsyncService prizesService) {
        this.asynchronousService = asynchronousService;
        this.prizesService = prizesService;
    }

    @GetMapping(value = "/{hour},{productId},{brandId}", produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<PrizeResponseDto> getPrizeAs(@PathVariable(value = "hour")
                                                 @DateTimeFormat(pattern = PrizeServiceUtils.FORMATO_FECHA) final Date hour,
                                                       @PathVariable(value = "productId") Integer productId,
                                                       @PathVariable(value = "brandId") Long brandId)
            throws PrizeNotFoundException, InterruptedException, ExecutionException {
        final PrizeRequestDto prizeFilterParams = new PrizeRequestDto(hour, productId, brandId);




            return new ResponseEntity<PrizeResponseDto>(prizesService.getCurrentPrizeByProductIdAndBrandId(prizeFilterParams).get()
                    ,HttpStatus.OK  );


    //    LOGGER.info("...asyncPrizeResponmse long running task done! " + LocalDateTime.now());

    }
}
//    private Future<PrizeResponseDto> getPrizeResponseDtoFuture(PrizeRequestDto prizeFilterParams) throws InterruptedException {
//        LOGGER.info("...getPrizeResponseDtoFuture long running task start!"+ LocalDateTime.now());
//        Future<PrizeResponseDto> respFuture = asynchronousService.asyncPrizeResponse(prizeFilterParams);
//
//
//        while (!respFuture.isDone()) {
//            LOGGER.info("...getPrizeResponseDtoFuture sleep!"+ LocalDateTime.now());
//            Thread.sleep(100);
//            LOGGER.info("...getPrizeResponseDtoFuture sleep end !"+ LocalDateTime.now());
//        }
//        LOGGER.info("...getPrizeResponseDtoFuture long running task done!"+ LocalDateTime.now()+" "+ Thread.currentThread().getName());
//        return respFuture;
//    }


