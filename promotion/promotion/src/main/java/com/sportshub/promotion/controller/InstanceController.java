package com.sportshub.promotion.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InstanceController {

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping("/instance")
    public Map<String, String> getInstance() {
        return Map.of(
                "service", applicationName,
                "port", serverPort
        );
    }
}