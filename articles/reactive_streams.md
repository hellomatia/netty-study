# reactive streams

[토비의 Spring - 스프링 리액티브 프로그래밍 (1)](https://www.youtube.com/live/8fenTR3KOJo?si=pVBiP2dO4p2zHjTb)

reactive programing이란 무엇을 의미할까?

나는 아직 이 질문에 제대로 된 답을 할 수 없을것 같다. 

reactive란 무엇인지도 잘 모르겠다.

토비님께서 reative streams에 대해 라이브 방송을 진행하신 적이 있는데, 이 영상들을 보면서 답을 찾아가볼 예정이다.

<br>

### Iterable

Iterable은 Collection이 implement하고 있는 Interface이다. 따라서 Set, List와 같이 Collection을 implement 하고 있는 클래스들은 모두 iterator 메서드를 가지고 있고, 이 메서드는 Iterator를 반환한다.

이 Iterator 은 for-each문에서 많이 쓰인다.

```java
    for (Iterator<Integer> it = iter.iterator(); it.hasNext();) {
        System.out.println(it.next());
    }
```

이렇게 쓰이는 for문이 아래와 같이 깔끔하게 변환된다.

```java
    for (Integer i : iter) {
        System.out.println(i);
    }
```

<br>

### Observable

Iterator의 쌍대성(duality)이 Observable이다.

Iterable은 next메서드를 통해 다음 값을 Pull해온다. 

하지만 Observable은 Push해온다. 

Observable은 Source이고 Event(Data)가 발생하면 Observer에게 던진다.

```java
class IntObservable extends Observable implements Runnable {
    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            setChanged();
            notifyObservers(i);         // push
            // int i = it.next();       // pull
        }
    }
}

class Ob {
    public static void main(String[] args) {
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(arg);
            }
        };
        
        IntObservable io = new IntObservable();
        io.addObserver(ob);
        
        io.run();
    }
}

```

1. IntObservable은 for문을 돌리면서 상태 변화를 Observer에게 알린다.

2. Observer은 변경된 상태를 출력하는 ob객체를 생성한다.

3. IntObservable에 ob를 등록시키고 run메서드를 호출하여 동작시킨다.

데이터를 넘기는 방식이 다르다. Iterator의 경우 pull을 하여 데이터를 반환하고, Observable은 파라미터로 데이터를 넘겨주어 push한다.

똑같은 기능인데, 구현 방식이 다르다.

Observable이 훨씬 다양하고, 다이나믹하다.

```java
class Ob {
    public static void main(String[] args) {
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + " " + arg);
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob);

        ExcutorService es = Executors.newSingleThreadExecutor();
        es.excute(io);

        System.out.println(Thread.currentThread().getName() + " EXIT");
        io.shutdown();
    }
}
```

이렇게 다른 Observable은 다른 스레드에 위임하고, 실행하면 결과는 어떻게 나올까?

```shell
main EXIT
pool-1-thread-1 1
pool-1-thread-1 2
pool-1-thread-1 3
pool-1-thread-1 4
pool-1-thread-1 5
pool-1-thread-1 6
pool-1-thread-1 7
pool-1-thread-1 8
pool-1-thread-1 9
pool-1-thread-1 10
```
결과를 보니 예상과 다른 결과가 나왔다. 분명히 io객체를 먼저 실행시켰는데, 왜 EXIT가 먼저 나올까?

별개의 스레드에서 동작하기 때문이다. 이렇게 Push방식으로 만들면 손 쉰게 다른 스레드에 위임시킬 수 있다.

하지만 Observable은 문제가 있다고 높으신 개발자들은 생각했다.

1. Complete가 없다! (언제 끝나는지 모르잖아, 끝을 알려줘야지)
2. Error 발생에 대한 것이 아무것도 없다.

이 두 가지를 추가해서 새롭게 Observable을 만들었다.

<br>

### reactive-streams
[reaciteve-streams](https://www.reactive-streams.org)

유명한 회사(Netflix, Twitter, RedHat 등,,,)이 모여 위의 단점들을 보안하고 새로운 표준을 만들었다.

RX Java, Project Reactor등등 다양하게 있지만, 위의 표준을 지키면서 구현되어 있다.

**Publisher**

Observable과 비슷한 역할을 한다.

**Subscriber**

Observer과 비슷한 역할을 한다. Publisher.subscribe(Subscriber)로 등록한다.

onSubscribe는 무조건 호출해야 한다.

onNext는 원하는 대로 호출이 가능하고, onError | onComplete 중 하나만 호출 가능하다.

```java
import java.util.concurrent.Executors;

public class PubSub {
    public static void main(String[] args) {
        Iterable<Integer> itr = Arryas.asList(1, 2, 3, 4, 5);
        ExecutorService es = Executors.newCachedThreadPool();

        Publisher p = new Publisher() {
            @Override
            public void subscirbe(Subscriber subscriber) {
                Iterator<Integer> it = itr.iterator();

                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        es.submit(() -> {
                            int i = 0;
                            try {
                                while (i++ < n) {
                                    if (it.hasNext()) {
                                        subscriber.onNext(it.next());
                                    } else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                }
                            } catch (RuntimeException e) {
                                subscriber.onError(e);
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Iteger> s = new Subscriber<Iteger>() {
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                this.subscription.request(2);
            }

            @Override
            public void onNext(Iteger item) {
                System.out.println("onNext " + item);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        p.subscribe(s);
    }
}

```

**Subscrption**

Publiser가 Subscriber에 비해 너무 빠를때 데이터를 어떻게 해야할까? 아마 버퍼를 엄청나게 늘려야 할 것이다.

그렇다면 차라리 Publisher에게 지연시켜 동작하도록 하는게 더 효율적이다.

이 속도를 조절하는 것이 백프레셔라고 한다.

request는 최대로 한번에 처리할 수 있는 개수를 정하는 것이다.

onSubscribe에서 최대 개수를 처리하고, 다음 처리는 onNext에서 처리한다.

한 Subscription에 대해서는 병렬적으로 처리하지는 않는다. 무조건 순서대로 데이터가 들어오는 것을 가정하고 만들어졌다.

