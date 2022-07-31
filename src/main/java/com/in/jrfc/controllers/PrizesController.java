package com.in.jrfc.controllers;

import com.in.jrfc.dtos.PrizeRequestDto;
import com.in.jrfc.dtos.PrizeResponseDto;
import com.in.jrfc.entities.Prize;
import com.in.jrfc.exceptions.PrizeNotFoundException;
import com.in.jrfc.repositories.PrizesRepository;
import com.in.jrfc.services.PrizesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/prizes")

public class PrizesController {
    private static final Logger logger = LogManager.getLogger(PrizesController.class);

    @Autowired
    private final PrizesService prizeService;
    @Autowired
    private final PrizesRepository prizesRepository;


    public PrizesController(PrizesService prizeService, PrizesRepository prizesRepository) {
        this.prizeService = prizeService;
        this.prizesRepository = prizesRepository;
    }

    @GetMapping(value = "/prizes")
    public ResponseEntity<List<Prize>> allPrizes() {
        List<Prize> prizes = prizesRepository.findAll();
        return (ResponseEntity.ok(prizes));

    }

    @GetMapping(value = "/current-prize")
    public ResponseEntity<PrizeResponseDto> getCurrentPrize(@RequestParam
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss") final Date hora,
                                                            @RequestParam Integer productId,
                                                            @RequestParam Long brandId) throws PrizeNotFoundException {
        final PrizeRequestDto prizeFilterParams = new PrizeRequestDto(hora, productId, brandId);
        return ResponseEntity.ok(prizeService.getCurrentPrizeByProductIdAndBrandId(prizeFilterParams));

    }

//    public ResponseEntity<PrizeResponseDto> getPrizeDetailBGrand,date(@PathVariable("id") int myId) {
//        PrizeResponseDto prize = prizeServiceImpl.getPrizeDetailsById(myId);
//        if (prize != null) {
//            return new ResponseEntity<PrizeBean>(prize, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<PrizeResponseDtoBean>(HttpStatus.NOT_FOUND);
//        }
//    }


}
