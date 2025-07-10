package org.pdzsoftware.payworld_account_manager.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.pdzsoftware.payworld_account_manager.exception.custom.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiExceptions(ApiException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getStatus(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherErrors(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong on the server",
                request.getRequestURI()
        );
        return ResponseEntity.status(error.getStatus()).body(error);
    }
}
