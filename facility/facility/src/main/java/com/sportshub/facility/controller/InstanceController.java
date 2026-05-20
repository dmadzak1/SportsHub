package com.sportshub.facility.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class InstanceController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/instance")
    public Map<String, Object> instance() {
        return Map.of(
                "service", "facility",
                "port", serverPort,
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @GetMapping("/test-delay/{milliseconds}")
    public Map<String, Object> testDelay(@PathVariable long milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);

        return Map.of(
                "service", "facility",
                "port", serverPort,
                "delayMs", milliseconds,
                "timestamp", LocalDateTime.now().toString()
        );
    }
}
