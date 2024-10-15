package com.study.flux;

import com.study.mono.MonoApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@EnableWebFlux
@SpringBootApplication
@RestController
@Slf4j
public class FluxApplication {
    @GetMapping("/event/{id}")
    Mono<List<Event>> event(@PathVariable int id) {
        List<Event> list = List.of(new Event(1L, "event1"), new Event(2L, "event2"));
        return Mono.just(list);
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events() {
        List<Event> list = List.of(new Event(1L, "event1"), new Event(2L, "event2"));
        return Flux.fromIterable(list);
    }

    public static void main(String[] args) {
        SpringApplication.run(FluxApplication.class, args);
    }

    @Data @AllArgsConstructor
    public static class Event {
        long id;
        String value;
    }
}
