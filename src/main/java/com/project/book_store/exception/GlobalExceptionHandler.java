package com.project.book_store.exception;

import com.project.book_store.payload.response.ResponseMessage;
import com.project.book_store.utils.Messages;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseMessage<String>> handleConflictException(ConflictException ex) {
        ResponseMessage<String> response = ResponseMessage.<String>builder()
                .message(ex.getMessage())
                .httpStatus(HttpStatus.CONFLICT)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }


    


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseMessage<String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ResponseMessage<String> response = ResponseMessage.<String>builder()
                .message(Messages.DUPLICATE_ENTRY_MESSAGE)
                .httpStatus(HttpStatus.CONFLICT)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }


    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseMessage<String>> handleBadCredentials(BadCredentialsException ex) {
        ResponseMessage<String> response = ResponseMessage.<String>builder()
                .message(Messages.BAD_CREDENTIALS_MESSAGE)
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ResponseMessage<String>> handleDisabledAccount(DisabledException ex) {
        ResponseMessage<String> response = ResponseMessage.<String>builder()
                .message(Messages.ACCOUNT_NOT_VERIFIED_LOGIN_MESSAGE)
                .httpStatus(HttpStatus.FORBIDDEN)
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ResponseMessage<String>> handleLockedAccount(LockedException ex) {
        ResponseMessage<String> response = ResponseMessage.<String>builder()
                .message(Messages.ACCOUNT_LOCKED_MESSAGE)
                .httpStatus(HttpStatus.FORBIDDEN)
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }


    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseMessage<String>> handleMalformedRequest(HttpMessageNotReadableException ex) {
        ResponseMessage<String> response = ResponseMessage.<String>builder()
                .message(Messages.MALFORMED_REQUEST_MESSAGE)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessage<String>> handleRuntimeException(RuntimeException ex) {
        ResponseMessage<String> response = ResponseMessage.<String>builder()
                .message(ex.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        }

        ResponseMessage<Map<String, String>> response = ResponseMessage.<Map<String, String>>builder()
                .object(validationErrors)
                .message(Messages.VALIDATION_ERROR_MESSAGE)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}