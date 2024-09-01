package com.study.netty.dummy;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class DummyApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(DummyApplication.class).run(args);
    }
}
