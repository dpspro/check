package com.in.jrfc.services;

import com.in.jrfc.dtos.PriceRequestDto;
import com.in.jrfc.dtos.PriceResponseDto;
import com.in.jrfc.entities.Price;
import com.in.jrfc.exceptions.PriceNotFoundException;
import com.in.jrfc.repositories.PriceRepository;
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
public class PriceAsyncService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PriceAsyncService.class);
    @Autowired
    PriceRepository priceRepository;

    public PriceAsyncService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @Async("asyncTaskExecutor")
    public Future<PriceResponseDto> getCurrentPriceByProductIdAndBrandId(PriceRequestDto priceFilterParams) throws PriceNotFoundException {
        try {
            LOGGER.info(" asyncPriceResponmse Start processing " + LocalDateTime.now());
            TimeUnit.MILLISECONDS.sleep(1000);
            return new AsyncResult<>(
                    entityToDto(
                            getPrice(priceFilterParams
                                    , priceRepository
                                            .findByProductIdAndBrandId(priceFilterParams.getProductId(), priceFilterParams.getBrandId())
                                            .stream()
                                            .filter(price -> price.validPriceRange(priceFilterParams.getRequestDate()))
                                            .collect(Collectors.toList()))
                            , priceFilterParams.getRequestDate()));


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    protected Price getPrice(PriceRequestDto priceFilterParams, List<Price> price) {
        boolean seen = false;
        Price best = null;
        Comparator<Price> comparator = Comparator.comparing(Price::getPriority);
        for (Price price1 : price) {
            if (!seen || comparator.compare(price1, best) > 0) {
                seen = true;
                best = price1;
            }
        }
        return (seen ? Optional.of(best) : Optional.<Price>empty())
                .orElseThrow(() -> new PriceNotFoundException(HttpStatus.NOT_FOUND,
                        "for productId :" + priceFilterParams.getProductId() + " and date " + priceFilterParams.getRequestDate()));
    }


    protected PriceResponseDto entityToDto(Price price, Date filterDate) {

        return new PriceResponseDto(price.getProductId(), price.getBrandId(), price.getPriceList()
                , price.lookForApplicationDates(filterDate), price.getPrice());

    }


}
