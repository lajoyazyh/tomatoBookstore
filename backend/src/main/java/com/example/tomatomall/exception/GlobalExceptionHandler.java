package com.example.tomatomall.exception;

import com.example.tomatomall.vo.ResultVO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = TomatoMallException.class)
    public ResultVO<String> handleAIExternalException(TomatoMallException e) {
        e.printStackTrace();
        return ResultVO.buildFailure(e.getMessage());
    }
}

