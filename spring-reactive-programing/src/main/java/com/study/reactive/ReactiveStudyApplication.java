package com.study.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@SpringBootApplication
@Slf4j
@EnableAsync
public class ReactiveStudyApplication {

    @RestController
    public static class MyController {
        @GetMapping("/callable")
        public Callable<String> callable() throws InterruptedException {
            log.info("callable");
            return () -> {
                log.info("async");
                Thread.sleep(2000);
                return "hello";
            };
        }

        @GetMapping("/async")
        public String async() throws InterruptedException {
            log.info("async");
            Thread.sleep(2000);
            return "hello";
        }
    }

    @Component
    public static class MyService {
        @Async("tp")
        public ListenableFuture<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(2000);
            return new AsyncResult<>("Hello");
        }
    }

    @Bean
    ThreadPoolTaskExecutor tp() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("my-thread-");
        executor.initialize();
        return executor;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveStudyApplication.class, args);
    }
}
