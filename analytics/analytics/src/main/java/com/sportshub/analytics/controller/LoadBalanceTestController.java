package com.sportshub.analytics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class LoadBalanceTestController {

    private final RestTemplate restTemplate;

    public LoadBalanceTestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/test/promotion-instance")
    public Map<String, Object> getPromotionInstance() {
        return restTemplate.getForObject("http://promotion/instance", Map.class);
    }
}