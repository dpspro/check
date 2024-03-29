package com.in.jrfc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PrizeNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(PrizeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String prizeNotFoundHandler(PrizeNotFoundException ex) {
        return ex.getMessage();
    }
}
