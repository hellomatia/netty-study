# reactive Streams Schedulers

[토비의 스프링 - reactive streams - Schedulers](https://www.youtube.com/live/Wlqu1xvZCak?si=O7TvjPs6v4gAsiNH)

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

public class SchedulerEx {
    public static void main(String[] args) {
        Flow.Publisher<Integer> pub = subscriber -> {
            subscriber.onSubscribe(new Flow.Subscription() {
                @Override
                public void request(long n) {
                    subscriber.onNext(1);
                    subscriber.onNext(2);
                    subscriber.onNext(3);
                    subscriber.onNext(4);
                    subscriber.onNext(5);
                    subscriber.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        };

        // pub

        Publisher<Integer> subOnPub = subscriber -> {
            ExecutorService es = Executors.newSingleThreadExecutor();
            es.execute(() -> pub.subscribe());
        };

        // sub

        pubOnPub.subscribe(new Flow.Subscriber<Integer>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext: " + item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError: " + throwable);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
        System.out.println("EXIT");
    }
} 
```

한 스레드에서 동작시키면 blocking이 발생하여 엄청 느려진다.

이를 해결하기 위해서 Scheduler를 사용한다.

새로운 스레드일 수 있고, 여러가지 옵션들이 있다.

위 코드에서 subOnPub은 다른 스레드에 일을 시키는 역할만 한다.

즉 Operator를 사용하여 중간에 subOnPub이 실행되는 것이다.

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

public class SchedulerEx {
    public static void main(String[] args) {
        Flow.Publisher<Integer> pub = subscriber -> {
            subscriber.onSubscribe(new Flow.Subscription() {
                @Override
                public void request(long n) {
                    subscriber.onNext(1);
                    subscriber.onNext(2);
                    subscriber.onNext(3);
                    subscriber.onNext(4);
                    subscriber.onNext(5);
                    subscriber.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        };

        // pub

        Publisher<Integer> subOnPub = subscriber -> {
            ExecutorService es = Executors.newSingleThreadExecutor();
            es.execute(() -> pub.subscribe());
        };

        Publisher<Integer> pubOnPub = subscriber -> {
            subOnPub.subscribe(new Flow.Subscriber<Integer>() {
                ExecutorService es = Executors.newSingleThreadExecutor();
                
                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    subscriber.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer item) {
                    es.execute(() -> subscriber.onNext(item));
                }

                @Override
                public void onError(Throwable throwable) {
                    es.execute(() -> subscriber.onError(throwable));
                }

                @Override
                public void onComplete() {
                    es.execute(() -> subscriber.onComplete());
                }
            });
        };

        // sub

        subOnPub.subscribe(new Flow.Subscriber<Integer>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext: " + item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError: " + throwable);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
        System.out.println("EXIT");
    }
} 
```
위 코드는 onNext, onError, onComplete는 별개의 스레드에서 실행시킨다.

생성은 빠른데, 소비가 느릴때 이러한 방법을 사용한다.

Reactor에서는 onPublisher를 사용할 수 있다.


```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

public class SchedulerEx {
    public static void main(String[] args) {
        Flow.Publisher<Integer> pub = subscriber -> {
            subscriber.onSubscribe(new Flow.Subscription() {
                @Override
                public void request(long n) {
                    subscriber.onNext(1);
                    subscriber.onNext(2);
                    subscriber.onNext(3);
                    subscriber.onNext(4);
                    subscriber.onNext(5);
                    subscriber.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        };

        // pub

        Publisher<Integer> pubOnPub = subscriber -> {
            pub.subscribe(new Flow.Subscriber<Integer>() {
                ExecutorService es = Executors.newSingleThreadExecutor();
                
                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    subscriber.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer item) {
                    es.execute(() -> subscriber.onNext(item));
                }

                @Override
                public void onError(Throwable throwable) {
                    es.execute(() -> subscriber.onError(throwable));
                }

                @Override
                public void onComplete() {
                    es.execute(() -> subscriber.onComplete());
                }
            });
        };

        // sub

        subOnPub.subscribe(new Flow.Subscriber<Integer>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext: " + item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError: " + throwable);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
        System.out.println("EXIT");
    }
} 
```

이 경우는 onSubcribe와 request의 스레드와 onNext, onComplete, onError를 다른 스레드에서 실행하도록 하는 것이다.

```java
public class FluxScEx {
    public static void main(String[] args) {
        Flux.range(1, 10)
                .log()
                .subscribe(System.out::println);
    }
}
```
위 코드는 main 스레드에서 모두 처리한다.

### Reactor에서는 어떻게 되어있을까?

```java
public class FluxScEx {
    public static void main(String[] args) {
        Flux.range(1, 10)
                .log()
                .subscribeOn(Schedulers.newSingle("sub"))
                .subscribe(System.out::println);
    }
}
```
subscribeOn을 사용하면 onSubscribe부터 complete를 다른 스레드에서 실행된다.

```java
public class FluxScEx {
    public static void main(String[] args) {
        Flux.range(1, 10)
                .publisherOn(Schedulers.newSingle("pub"))
                .log()
                .subscribeOn(Schedulers.newSingle("sub"))
                .subscribe(System.out::println);
    }
}
```
publisherOn을 사용하면 onNext부터 complete를 다른 스레드에서 실행된다.

```java
import java.time.Duration;

public class FluxScEx {
    public static void main(String[] args) {
        Flux.interval(Duration.ofMillis(500))
                .subscribe(System.out::println);
    }
}
```
이 코드는 실행이 될까?

실행을 해보면 하나도 실행이 안되고, 바로 종료가 된다? 

위 예제들을 자세히 살펴봤다면, 정답을 알게 된다.

```java
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class FluxScEx {
    public static void main(String[] args) {
        Flux.interval(Duration.ofMillis(500))
                .subscribe(System.out::println);

        TimeUnit.SECONDS.sleep(5);
    }
}
```

이 코드는 어느정도 실행되고 종료가 된다.

Flux.interval은 User 스레드를 사용하지 않고, 데몬 스레드를 사용된다.

유저 스레드가 하나라도 존재하면, 종료되지 않는다.

따라서 Main 스레드가 종료된다고 바로 종료되는 건 아니다.

```java
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class FluxScEx {
    public static void main(String[] args) {
        Flux.interval(Duration.ofMillis(200))
                .take(10)
                .subscribe(System.out::println);

        TimeUnit.SECONDS.sleep(5);
    }
}
```

take를 사용하면 10개만 받고 종료시킨다.