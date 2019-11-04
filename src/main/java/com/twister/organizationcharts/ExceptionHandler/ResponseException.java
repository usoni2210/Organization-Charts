package com.twister.organizationcharts.ExceptionHandler;

import java.util.Date;

public class ResponseException {
    private String details;
    private String message;
    private Date timestamp;

    public ResponseException(String message, String details) {
        this.details = details;
        this.message = message;
        this.timestamp = new Date();
    }

    public String getDetails() {
        return details;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
