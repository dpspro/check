package com.in.jrfc.services;

import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import com.in.jrfc.entities.Prize;
import com.in.jrfc.exceptions.PrizeNotFoundException;
import com.in.jrfc.repositories.PrizesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Transactional(readOnly = true)
@Service
public class PrizesAsyncService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PrizesAsyncService.class);
    @Autowired
    PrizesRepository prizesRepository;

    public PrizesAsyncService(PrizesRepository prizeRepository) {
        this.prizesRepository = prizeRepository;
    }

    @Async("asyncTaskExecutor")
    public Future<PrizeResponseDto> getCurrentPrizeByProductIdAndBrandId(PrizeRequestDto prizeFilterParams) throws PrizeNotFoundException {
        try {
            LOGGER.info(" asyncPrizeResponmse Start processing " + LocalDateTime.now());
            TimeUnit.MILLISECONDS.sleep(1000);
            return new AsyncResult<>(
                    entityToDto(
                            getPrize(prizeFilterParams
                                    , prizesRepository
                                            .findByProductIdAndBrandId(prizeFilterParams.getProductId(), prizeFilterParams.getBrandId())
                                            .stream()
                                            .filter(prize -> prize.validPrizeRange(prizeFilterParams.getRequestDate()))
                                            .collect(Collectors.toList()))
                            , prizeFilterParams.getRequestDate()));


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    protected Prize getPrize(PrizeRequestDto prizeFilterParams, List<Prize> prizes) {
        boolean seen = false;
        Prize best = null;
        Comparator<Prize> comparator = Comparator.comparing(Prize::getPriority);
        for (Prize prize1 : prizes) {
            if (!seen || comparator.compare(prize1, best) > 0) {
                seen = true;
                best = prize1;
            }
        }
        return (seen ? Optional.of(best) : Optional.<Prize>empty())
                .orElseThrow(() -> new PrizeNotFoundException(HttpStatus.NOT_FOUND,
                        "for productId :" + prizeFilterParams.getProductId() + " and date " + prizeFilterParams.getRequestDate()));
    }


    protected PrizeResponseDto entityToDto(Prize prize, Date filterDate) {

        return new PrizeResponseDto(prize.getProductId(), prize.getBrandId(), prize.getPrizeList()
                , prize.lookForApplicationDates(filterDate), prize.getPrize());

    }


}
