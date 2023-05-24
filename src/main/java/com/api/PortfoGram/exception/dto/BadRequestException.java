package com.api.PortfoGram.exception.dto;

public class BadRequestException extends BaseException {

    public BadRequestException(ExceptionEnum code) {
        super(code);
    }

    public BadRequestException(ExceptionEnum code, String message) {
        super(code, message);
    }
    public BadRequestException( String message) {
        super( message);
    }


}
