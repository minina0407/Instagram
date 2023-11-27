package com.api.PortfoGram.exception.handler;


import com.api.PortfoGram.exception.dto.BaseException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.exception.dto.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.ServletException;


@RestControllerAdvice
public class GlobalExceptionHandler  {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> MethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getFieldError().getDefaultMessage();
        ExceptionEnum code = ExceptionEnum.REQUEST_PARAMETER_INVALID;
        return new ResponseEntity<>(response(code, errorMessage), code.getStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response> MethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.error("Exception",e);
        ExceptionEnum code = ExceptionEnum.REQUEST_PARAMETER_MISSING;
        return new ResponseEntity<>(response(code, code.getReason()), code.getStatus());
    }

    @ExceptionHandler(BaseException.class)
    @Nullable
    public final ResponseEntity<Response> handleException(BaseException e) {
        ExceptionEnum code= e.getCode();
        logger.error("Exception Occurred", e);
        return new ResponseEntity<>(response(code, e.getMessage()), code.getStatus());
    }

    @ExceptionHandler(ServletException.class)
    protected ResponseEntity<Response> handleNestedServletException(Exception e) {
        ExceptionEnum code = ExceptionEnum.RESPONSE_INTERNAL_SEVER_ERROR;

        logger.error("Servlet Exception", e);
        return new ResponseEntity<>(response(code, e.getMessage()), code.getStatus());
    }
    @ExceptionHandler(Exception.class)
    @Nullable
    public final ResponseEntity<Response> handleOtherException(Exception e)  {
        ExceptionEnum code = ExceptionEnum.RESPONSE_INTERNAL_SEVER_ERROR;
        logger.error("Unknown Internal Exception Occurred", e);
        return new ResponseEntity<>(response(code,e.getMessage()),  code.getStatus());
    }

    Response response(ExceptionEnum code, String message) {
        return Response.builder()
                .code(code.getCode())
                .description(message)
                .build();
    }

}

