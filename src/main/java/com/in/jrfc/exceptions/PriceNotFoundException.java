package com.in.jrfc.exceptions;

import org.springframework.http.HttpStatus;

public class PriceNotFoundException extends RuntimeException {
    private static final String DESCRIPTION = "Price Not Found Exception";
    public PriceNotFoundException(HttpStatus notFound, String detail) {

        super(DESCRIPTION + ". " + detail);
    }

    public PriceNotFoundException() {
    }
}
