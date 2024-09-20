package com.study.asyncrest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {
    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
//        restTest();
        webClientTest();
    }

    public static void restTest() throws InterruptedException, BrokenBarrierException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/rest?idx={idx}";

        CyclicBarrier barrier = new CyclicBarrier(101);

        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                int idx = counter.incrementAndGet();

                barrier.await();

                log.info("Thread {}", idx);

                StopWatch stopWatch = new StopWatch();
                stopWatch.start();

                String res = rt.getForObject(url, String.class, idx);

                stopWatch.stop();
                log.info("Elapsed time: {} -> {} / {}", idx, stopWatch.getTotalTimeSeconds(), res);
                return null;
            });
        }

        barrier.await();
        StopWatch main = new StopWatch();
        main.start();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Elapsed Total time: {}", main.getTotalTimeMillis());
    }

    public static void webClientTest() throws InterruptedException, BrokenBarrierException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/webclient?idx={idx}";

        CyclicBarrier barrier = new CyclicBarrier(101);

        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                int idx = counter.incrementAndGet();

                barrier.await();

                log.info("Thread {}", idx);

                StopWatch stopWatch = new StopWatch();
                stopWatch.start();

                String res = rt.getForObject(url, String.class, idx);

                stopWatch.stop();
                log.info("Elapsed time: {} -> {} / {}", idx, stopWatch.getTotalTimeSeconds(), res);
                return null;
            });
        }

        barrier.await();
        StopWatch main = new StopWatch();
        main.start();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Elapsed Total time: {}", main.getTotalTimeMillis());
    }
}
