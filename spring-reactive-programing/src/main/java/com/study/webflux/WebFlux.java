package com.study.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@EnableWebFlux
@SpringBootApplication
public class WebFlux {

    public static void main(String[] args) {
        System.setProperty("spring.main.web-application-type", "reactive");
        SpringApplication.run(WebFlux.class, args);
    }

    @Slf4j
    @RestController
    static class MyController {

        @Autowired
        MyService myService;

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
                    .flatMap(c -> c.bodyToMono(String.class))
                    .flatMap(res2 -> Mono.fromCompletionStage(myService.work(res2)))
                    .doOnNext(c -> log.info(c));
        }
    }

    @Service
    public static class MyService {
        @Async
        public CompletableFuture<String> work(String req) {
            return CompletableFuture.completedFuture(req + "/asyncwork");
        }
    }
}

