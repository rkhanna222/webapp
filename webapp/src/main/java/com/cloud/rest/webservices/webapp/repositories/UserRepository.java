package com.cloud.rest.webservices.webapp.repositories;

import com.cloud.rest.webservices.webapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

    @Query("SELECT count(username) FROM User WHERE username=:username")
    int isEmailPresent(@Param("username") String username);

    User findByUsername(String email);









}
