package com.api.instagram.exception.dto;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private ExceptionEnum code;
    public BaseException(String message){
        super(message);
    }
    public BaseException(ExceptionEnum code){
       super(code.getReason());
       this.code = code;
    }
    public BaseException(ExceptionEnum code, String message) {
        super(message);
        this.code = code;
    }
}
