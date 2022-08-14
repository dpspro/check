package com.in.jrfc.services;

import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
@Deprecated
@Service
public class AsynchronousService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AsynchronousService.class);
    @Autowired
    final PrizesService prizeService;

    public AsynchronousService(PrizesService prizeService) {
        this.prizeService = prizeService;
    }

//    @Async("asyncTaskExecutor")
    public Future<PrizeResponseDto> asyncPrizeResponse(final PrizeRequestDto prizeRequestDto) {

        try {
            LOGGER.info(" asyncPrizeResponmse Start processing "+ LocalDateTime.now());
            TimeUnit.MILLISECONDS.sleep(1000);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("...asyncPrizeResponmse long running task done! "+ LocalDateTime.now());
        return new AsyncResult<>(prizeService.getCurrentPrizeByProductIdAndBrandId(prizeRequestDto));
    }


}

