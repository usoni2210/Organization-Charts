package com.twister.organizationcharts.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DesignationException extends RuntimeException {
    private final HttpStatus status;

    public DesignationException() {
        super("Designation : Designation Not Found");
        this.status = HttpStatus.NOT_FOUND;
    }

    public DesignationException(String message, HttpStatus status) {
        super("Designation : " + message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
