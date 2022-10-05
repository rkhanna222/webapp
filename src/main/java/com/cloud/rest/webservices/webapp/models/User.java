package com.cloud.rest.webservices.webapp.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="User")
public class User implements Serializable{

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;


    @JsonProperty("username")
    @NotBlank(message = "username is not entered")
    private String username;


    @JsonProperty("first_name")
    @NotBlank(message="Firstname is required")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message="Lastname is required")
    private String lastName;

    @JsonProperty("account_created")
    private LocalDateTime accountCreated;

    @JsonProperty("account_updated")
    private LocalDateTime accountUpdated;



    @JsonIgnore
    @JsonProperty("password")
    private String password;

    public User() {
    }

    public User(UUID id, String username, String firstName, String lastName, String password, LocalDateTime accountCreated,LocalDateTime accountUpdated) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.accountCreated =accountCreated;
        this.accountUpdated = accountUpdated;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        return password;
    }

    public void setPassword(String pass) {
        this.password = pass;
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
                ", emailID='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
