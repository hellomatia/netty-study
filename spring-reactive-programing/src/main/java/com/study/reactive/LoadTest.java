package com.study.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {
    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/callable";

        StopWatch main = new StopWatch();
        main.start();

        for (int i = 0; i < 100; i++) {
            es.execute(() -> {
                int idx = counter.incrementAndGet();
                log.info("Thread {}", idx);

                StopWatch stopWatch = new StopWatch();
                stopWatch.start();

                rt.getForObject(url, String.class);

                stopWatch.stop();
                log.info("Elapsed time: {} -> {}", idx, stopWatch.getTotalTimeMillis());
            });
        }

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Elapsed Total time: {}", main.getTotalTimeMillis());
    }
}
