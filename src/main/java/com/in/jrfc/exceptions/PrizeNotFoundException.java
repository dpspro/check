package com.in.jrfc.exceptions;

import org.springframework.http.HttpStatus;

public class PrizeNotFoundException extends RuntimeException {
    private static final String DESCRIPTION = "Prize Not Found Exception";
    public PrizeNotFoundException(HttpStatus notFound, String detail) {

        super(DESCRIPTION + ". " + detail);
    }

    public PrizeNotFoundException() {
    }
}
