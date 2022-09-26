package com.cloud.rest.webservices.webapp.controllers;

public class Health {

    private String message;

    public Health(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Health{" +
                "message='" + message + '\'' +
                '}';
    }
}
