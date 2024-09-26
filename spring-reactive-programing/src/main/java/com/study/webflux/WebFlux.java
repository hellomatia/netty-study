package com.study.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@EnableWebFlux
@SpringBootApplication
public class WebFlux {

    public static void main(String[] args) {
        System.setProperty("spring.main.web-application-type", "reactive");
        SpringApplication.run(WebFlux.class, args);
    }

    @RestController
    static class MyController {
        WebClient client = WebClient.create();

        @GetMapping("/webflux")
        public Mono<String> webflux(int idx) {
            // Mono는 Publisher이다.
            // 비동기 작업을 수행하고 Publisher하는 역할을 하는거다.
            // 하지만 누가 Subscribe를 하지 않으면 Publisher하지 않는다.
            return client.get()
                    .uri("http://localhost:8081/service1?req={req}", idx)
                    .exchange()
                    .flatMap(c -> c.bodyToMono(String.class))
                    .flatMap(res1 -> client.get().uri("http://localhost:8081/service1?req={req}", res1).exchange())
                    .flatMap(c -> c.bodyToMono(String.class));
        }
    }
}

