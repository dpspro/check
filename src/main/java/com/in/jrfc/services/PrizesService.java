package com.in.jrfc.services;

import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import com.in.jrfc.entities.Prize;
import com.in.jrfc.exceptions.PrizeNotFoundException;
import com.in.jrfc.repositories.PrizesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrizesService {

    @Autowired
    PrizesRepository prizesRepository;

    public PrizesService(PrizesRepository prizeRepository) {
        this.prizesRepository = prizeRepository;
    }

    @Transactional(readOnly = true)
    public PrizeResponseDto getCurrentPrizeByProductIdAndBrandId(PrizeRequestDto prizeFilterParams) throws PrizeNotFoundException {

        List<Prize> prizes = prizesRepository
                .findByProductIdAndBrandId(prizeFilterParams.getProductId(),prizeFilterParams.getBrandId())
                .stream()
                .filter(prize -> prize.validPrizeRange(prizeFilterParams.getRequestDate()))
                .collect(Collectors.toList());
        Prize prize = getPrize(prizeFilterParams, prizes);

        return entityToDto(prize,prizeFilterParams.getRequestDate());
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
        var prize = (seen ? Optional.of(best) : Optional.<Prize>empty())
                .orElseThrow(() -> new PrizeNotFoundException(HttpStatus.NOT_FOUND,
                        "for productId :" + prizeFilterParams.getProductId() + " and date " + prizeFilterParams.getRequestDate()));
        return prize;
    }


     protected PrizeResponseDto entityToDto(Prize prize, Date filterDate) {

        return new PrizeResponseDto(prize.getProductId(), prize.getBrandId(), prize.getPrizeList()
                , prize.lookForApplicationDates(filterDate), prize.getPrize());

    }


}
