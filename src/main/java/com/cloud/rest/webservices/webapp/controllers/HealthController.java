package com.cloud.rest.webservices.webapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping(path = "/healthz")
    public void HealthCheck(){
    }
}
