package com.recap.mvc;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class AppTest {

    private static RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        String result = restTemplate.getForObject("http://localhost:5678/hello", String.class);
        System.out.println(result);

        HttpHeaders headers = new HttpHeaders();
        headers.set("CustomHeader", "hi");

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        result = restTemplate.postForObject("http://localhost:5678/header", entity, String.class);
        System.out.println(result);


        HttpEntity<Body> entity2 = new HttpEntity<>(new Body("hello"), headers);
        result = restTemplate.postForObject("http://localhost:5678/body", entity2, String.class);
        System.out.println(result);
    }

    static class Body {
        private String name;

        public Body(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
