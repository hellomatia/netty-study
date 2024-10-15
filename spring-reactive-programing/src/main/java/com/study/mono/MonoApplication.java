package com.study.mono;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@Slf4j
public class MonoApplication {

    @GetMapping("/")
    Mono<String> hello() {
        log.info("pos1");
//        String msg = generateHello();
//        Mono m = Mono.just(msg).log();
        Mono<String> m = Mono.fromSupplier(this::generateHello).log();
        String msg2 = m.block();
        log.info(msg2);
        log.info("pos2");
        return m;
    }

    private String generateHello() {
        log.info("Generating Hello");
        return "Hello";
    }

    public static void main(String[] args) {
        SpringApplication.run(MonoApplication.class, args);
    }
}
