package com.api.instagram.exception.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionEnum {
    REQUEST_PARAMETER_MISSING("400", "파라미터가 없습니다.", HttpStatus.BAD_REQUEST),
    REQUEST_PARAMETER_INVALID("400", "파라미터가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    RESPONSE_SUCCESS("200", "성공입니다.", HttpStatus.OK),
    RESPONSE_UNAUTHORIZED("401", "권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    RESPONSE_TOKEN_EXPIRED("401", "토큰의 유효기간이 아닙니다.", HttpStatus.UNAUTHORIZED),
    RESPONSE_TOKEN_INVALID("401", "유효한 토큰이 아닙니다.", HttpStatus.UNAUTHORIZED),
    RESPONSE_NOT_FOUND("404", "대상을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RESPONSE_INTERNAL_SEVER_ERROR("502", "서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String reason;
    private final HttpStatus status;

    ExceptionEnum(String code, String reason, HttpStatus status) {
        this.code = code;
        this.reason = reason;
        this.status = status;
    }
}

