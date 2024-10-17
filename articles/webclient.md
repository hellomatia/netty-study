# WebClient

## Spring MVC에서의 Webclient동작 방식
보통 Spring MVC에서 Webclient를 사용하게 되면, block()을 사용한다.

그렇게 된다면 해당 스레드는 block상태로 되어서 비동기, 논블록킹의 이점이 사라질 수 있다.

그럼에도 이점이 있을까?

```asciidoc
[HTTP 요청] → [Tomcat 스레드] → [WebClient(Netty)] → [외부 API] 
                    ↑ block()                ↓
                    └────────── 응답 ────────┘
```

### 1. 초기 요청 처리
```java
@GetMapping("/api")
public Response callExternalApi() {
    // Tomcat 스레드가 여기서 실행
    return webClient.get()
            .uri("/external-api")
            .retrieve()
            .bodyToMono(Response.class)
            .block();  // 여기서 Tomcat 스레드가 블로킹됨
}
```
### 2. 내부 동작 순서
- Tomcat 스레드가 요청을 받음
- WebClient가 Netty의 EventLoop를 사용하여 비동기로 외부 API 호출
- block() 호출로 인해 Tomcat 스레드는 대기 상태로 진입
- Netty EventLoop는 계속해서 비동기로 동작
- 응답이 오면 Tomcat 스레드가 깨어나서 결과를 반환

### 3. 실제 스레드 동작
```java
// Tomcat 스레드
Thread tomcatThread = Thread.currentThread();
// block() 호출 시점에서
CountDownLatch latch = new CountDownLatch(1); // 내부적으로 이와 유사한 동기화 사용

// Netty EventLoop 스레드 (비동기 실행)
eventLoop.execute(() -> {
    try {
        // HTTP 호출 수행
        // 응답 수신
        result = processResponse(response);
        latch.countDown(); // Tomcat 스레드를 깨움
    } catch (Exception e) {
        // 예외 처리
    }
});
```
Netty의 장점이 일부만 활용됨
- 비동기 네트워크 I/O는 유지
- Connection Pooling 이점은 유지
- 하지만 Tomcat 스레드는 블로킹됨

WebClient는 내부적으로 네트워크 연결관리를 비동기적으로 처리한다.

즉, Netty는 여전히 비동기로 네트워크 작업을 수행한다.

하지만 Tomcat스레드가 블로킹되어 대기한다.

## WebFlux에서 WebClient 사용하기
### Spring MVC vs Spring WebFlux

```asciidoc
[Spring MVC + WebClient]
Request → [Tomcat Thread Pool] → [WebClient/Netty] → [External API]
           (블로킹 발생)          (비동기)

[Spring WebFlux + WebClient]
Request → [Event Loop Thread] → [WebClient/Netty] → [External API]
          (논블로킹)             (비동기)
```

### 코드 비교
```java
// Spring MVC + WebClient (블로킹)
@RestController
public class MvcController {
    @GetMapping("/api")
    public Response blockingCall() {
        // Tomcat 스레드가 블로킹됨
        return webClient.get()
                .uri("/external-api")
                .retrieve()
                .bodyToMono(Response.class)
                .block(); // 여기서 블로킹
    }
}

// Spring WebFlux + WebClient (논블로킹)
@RestController
public class WebFluxController {
    @GetMapping("/api")
    public Mono<Response> reactiveCall() {
        // Event Loop 스레드가 논블로킹으로 처리
        return webClient.get()
                .uri("/external-api")
                .retrieve()
                .bodyToMono(Response.class); // 논블로킹 유지
    }
}
```

### WebFlux + WebClient 조합이:
- 더 효율적인 리소스 사용
- 더 높은 동시성
- 더 나은 백프레셔 제어
- 더 유연한 에러 처리를 제공


### 하지만 고려사항:
- 러닝커브가 있음
- 디버깅이 더 복잡할 수 있음
- 기존 동기식 라이브러리와의 통합이 어려울 수 있음

따라서, 새로운 프로젝트나 고성능이 필요한 경우 WebFlux를 고려해볼 만 하지만, 기존 MVC 프로젝트의 경우 전환 비용을 고려해야한다.