package com.example.car.sharing.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();
        ResponseBody responseBody = new ResponseBody(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST, errors);
        return new ResponseEntity<>(responseBody, headers, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex
    ) {
        ResponseBody responseBody = new ResponseBody(LocalDateTime.now(),
                HttpStatus.NOT_FOUND, List.of(ex.getMessage()));
        return new ResponseEntity<>(responseBody,
                HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            String field = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            return field + " " + message;
        }
        return e.getDefaultMessage();
    }

    @Data
    private class ResponseBody {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime timestamp;
        private HttpStatus status;
        private List<String> errors;

        public ResponseBody(LocalDateTime timestamp, HttpStatus status, List<String> errors) {
            this.timestamp = timestamp;
            this.status = status;
            this.errors = errors;
        }
    }
}
