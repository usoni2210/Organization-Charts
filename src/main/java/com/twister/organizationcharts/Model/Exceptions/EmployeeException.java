package com.twister.organizationcharts.Model.Exceptions;

import org.springframework.http.HttpStatus;

public class EmployeeException extends RuntimeException {

    private HttpStatus status;

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
