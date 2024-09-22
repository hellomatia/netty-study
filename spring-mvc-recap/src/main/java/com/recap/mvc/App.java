package com.recap.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RestController
    static class ApiController {
        @GetMapping("/hello")
        public ResponseEntity<?> hello() {
            return ResponseEntity.ok("Hello World");
        }

        @PostMapping("/header")
        public ResponseEntity<?> header(@RequestHeader String customHeader) {
            return ResponseEntity.ok(customHeader);
        }

        @PostMapping("/body")
        public ResponseEntity<?> body(@RequestBody Map<String, String> body) {
            return ResponseEntity.ok(body);
        }
    }
}
