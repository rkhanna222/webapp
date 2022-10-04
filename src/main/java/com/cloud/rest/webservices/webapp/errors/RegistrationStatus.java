package com.cloud.rest.webservices.webapp.errors;



public class RegistrationStatus {
    private String usernameError;
    private String passwordError;

    private String firstNameError;
    private String lastNameError;

    public RegistrationStatus() {
        usernameError = "-";
        passwordError = "-";
        firstNameError= "-";
        lastNameError = "-";
    }

    public RegistrationStatus(String usernameError, String passwordError, String firstNameError, String lastNameError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.firstNameError = firstNameError;
        this.lastNameError = lastNameError;
    }

    public String getUsernameError() {
        return usernameError;
    }

    public void setUsernameError(String usernameError) {
        this.usernameError = usernameError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }

    public String getFirstNameError() {
        return firstNameError;
    }

    public void setFirstNameError(String firstNameError) {
        this.firstNameError = firstNameError;
    }

    public String getLastNameError() {
        return lastNameError;
    }

    public void setLastNameError(String lastNameError) {
        this.lastNameError = lastNameError;
    }
}
