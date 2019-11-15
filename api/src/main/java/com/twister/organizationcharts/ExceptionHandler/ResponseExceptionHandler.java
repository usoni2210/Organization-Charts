package com.twister.organizationcharts.ExceptionHandler;

import com.twister.organizationcharts.Model.Exceptions.DesignationException;
import com.twister.organizationcharts.Model.Exceptions.EmployeeException;
import com.twister.organizationcharts.Model.ResponseExceptionBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@ControllerAdvice
@RestController
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleALLExceptions(Exception ex, WebRequest webRequest) {
        ResponseExceptionBean exceptionResponse = new ResponseExceptionBean(ex.toString(), webRequest.getDescription(false));
        ex.printStackTrace();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ResponseExceptionBean responseExceptionBean = new ResponseExceptionBean(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(responseExceptionBean, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ResponseExceptionBean exceptionResponse = new ResponseExceptionBean(
                Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage(),
                request.getDescription(false)
        );
        ex.printStackTrace();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmployeeException.class)
    public final ResponseEntity<Object> handleEmployeeNotFoundExceptions(EmployeeException ex, WebRequest webRequest) {
        ResponseExceptionBean exceptionResponse = new ResponseExceptionBean(ex.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, ex.getStatus());
    }

    @ExceptionHandler(DesignationException.class)
    public final ResponseEntity<Object> handleDesignationNotFoundExceptions(DesignationException ex, WebRequest webRequest) {
        ResponseExceptionBean exceptionResponse = new ResponseExceptionBean(ex.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, ex.getStatus());
    }
}
