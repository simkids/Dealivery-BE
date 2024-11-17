package org.example.backend.global.exception;


import org.example.backend.global.common.constants.BaseResponse;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(value = 1)
public class GlobalExceptionHandler {


    //커스텀 예외 핸들러(BaseResponse 기반)
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<String> handleGlobalException(InvalidCustomException e) {
        return new BaseResponse<>(e.getStatus());
    }

    //@Valid 실패시 예외 핸들러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST).body(new BaseResponse(BaseResponseStatus.FAIL));
    }

    //DB작업 실패시 (제약조건 위반) 예외 핸들러
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT).body(new BaseResponse(BaseResponseStatus.FAIL));
    }
}
