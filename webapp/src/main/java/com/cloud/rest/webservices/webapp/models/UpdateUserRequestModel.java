package com.cloud.rest.webservices.webapp.models;

import jakarta.validation.constraints.NotNull;

public class UpdateUserRequestModel {

    @NotNull(message = "Firstname cannot be empty")
    private String firstName;
    @NotNull(message = "Lastname cannot be empty")
    private String lastName;

    @NotNull(message = "Password cannot be empty")
    private String password;
}
