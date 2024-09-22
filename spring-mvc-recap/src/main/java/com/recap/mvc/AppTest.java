package com.recap.mvc;

import org.springframework.web.client.RestTemplate;

public class AppTest {

    private static RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        String result = restTemplate.getForObject("http://localhost:5678/hello", String.class);
        System.out.println(result);
    }
}
