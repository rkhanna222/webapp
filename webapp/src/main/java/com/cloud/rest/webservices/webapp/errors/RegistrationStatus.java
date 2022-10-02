package com.cloud.rest.webservices.webapp.errors;



public class RegistrationStatus {
    private String emailIdError;
    private String passwordError;

    public RegistrationStatus() {
        emailIdError = "-";
        passwordError = "-";
    }

    public RegistrationStatus(String emailIdError, String passwordError) {
        this.emailIdError = emailIdError;
        this.passwordError = passwordError;
    }

    public String getEmailIdError() {
        return emailIdError;
    }

    public void setEmailIdError(String emailIdError) {
        this.emailIdError = emailIdError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }
}
