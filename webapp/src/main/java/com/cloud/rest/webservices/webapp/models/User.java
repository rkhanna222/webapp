package com.cloud.rest.webservices.webapp.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="user_tbl")
public class User {

    @Id
    @GeneratedValue
    private UUID id;


    @JsonProperty("username")
    private String emailID;


    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("account_created")
    private LocalDateTime accountCreated;

    @JsonProperty("account_updated")
    private LocalDateTime accountUpdated;



    @JsonIgnore
    private String pass;

    public User() {
    }

    public User(UUID id, String emailID, String firstName, String lastName, String pass, LocalDateTime accountCreated,LocalDateTime accountUpdated) {
        this.id = id;
        this.emailID = emailID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pass = pass;
        this.accountCreated =accountCreated;
        this.accountUpdated = accountUpdated;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonIgnore
    public String getPassword() {
        return pass;
    }

    public void setPassword(String pass) {
        this.pass = pass;
    }


    public LocalDateTime getAccountCreated() {
        return accountCreated;
    }

    public void setAccountCreated(LocalDateTime accountCreated) {
        this.accountCreated = accountCreated;
    }

    public LocalDateTime getAccountUpdated() {
        return accountUpdated;
    }

    public void setAccountUpdated(LocalDateTime accountUpdated) {
        this.accountUpdated = accountUpdated;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", emailID='" + emailID + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
