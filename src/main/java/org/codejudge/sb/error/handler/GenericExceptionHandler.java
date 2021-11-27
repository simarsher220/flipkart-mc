package org.codejudge.sb.error.handler;

import org.codejudge.sb.error.exception.CustomException;
import org.codejudge.sb.error.model.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(1)
@ControllerAdvice
public class GenericExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity handleCustomException(CustomException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), exception.getStatus());
    }
}
