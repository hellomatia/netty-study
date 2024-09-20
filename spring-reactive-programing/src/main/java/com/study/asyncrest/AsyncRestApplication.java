package com.study.asyncrest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
public class AsyncRestApplication {

    @RestController
    public static class MyController {
        RestTemplate rt = new RestTemplate();
        WebClient wc = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .build();


        // Too Slow !!
        @GetMapping("/rest")
        public String rest(@RequestParam(name = "idx", required = false) int idx) {
            String res = rt.getForObject("http://localhost:8081/service1?req={req}", String.class,"hello " + idx);
            return res;
        }

        @GetMapping("/webclient")
        public Mono<String> asyncRest(@RequestParam(name = "idx", required = false) int idx) {
            Mono<String> result1 = wc.get()
                    .uri(ub -> ub
                            .path("/service1")
                            .queryParam("req", "hello " + idx)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class);

            Mono<String> result2 = wc.get()
                    .uri(ub -> ub
                            .path("/service2")
                            .queryParam("req", "hello " + idx)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class);

            return Mono.zip(result1, result2)
                    .map(t -> t.getT1() + " " + t.getT2());
        }
    }

    @Bean
    public ThreadPoolTaskExecutor myThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        return executor;
    }

    public static void main(String[] args) {
        SpringApplication.run(AsyncRestApplication.class, args);
    }
}
