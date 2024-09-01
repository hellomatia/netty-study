package com.study.netty.dummy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class DummyController {

    @GetMapping
    public Mono<String> dummy() {
        return Mono.just("dummy");
    }
}
