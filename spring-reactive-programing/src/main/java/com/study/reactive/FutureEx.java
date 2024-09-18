package com.study.reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
public class FutureEx {

    interface SuccessCallback {
        void onSuccess(String result);
    }

    interface ExceptionCallback {
        void onError(Throwable t);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback sc;
        ExceptionCallback ec;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                ec.onError(e.getCause());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutureTask f = new CallbackFutureTask(() -> {
            Thread.sleep(2000);
            if (1 == 1) throw new RuntimeException("Async Error!!");
            log.info("Async");
            return "Hello";
        }, s -> log.info(s), e -> log.error(e.getMessage()));

//        FutureTask<String> f = new FutureTask<>(() -> {
//            Thread.sleep(2000);
//            log.info("Async");
//            return "Hello";
//        }) {
//            @Override
//            protected void done() {
//                try {
//                    log.info(get());
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                } catch (ExecutionException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        };

        es.execute(f);
        es.shutdown();

        log.info(String.valueOf(f.isDone()));
        Thread.sleep(2100);
        log.info("Exit");

        log.info(String.valueOf(f.isDone()));
        // Future의 get은 Blocking 상태에 계속 대기한다.
        log.info(f.get());
    }
}
