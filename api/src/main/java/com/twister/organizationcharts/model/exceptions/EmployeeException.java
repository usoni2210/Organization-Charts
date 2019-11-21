package com.twister.organizationcharts.model.exceptions;

import org.springframework.http.HttpStatus;

public class EmployeeException extends RuntimeException {

    private final HttpStatus status;

    public EmployeeException() {
        super("Employee : Employee Not Found");
        this.status = HttpStatus.NOT_FOUND;
    }

    public EmployeeException(String message, HttpStatus status) {
        super("Employee : " + message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
