# 부트스트랩

[자바 네트워크 소녀 Netty](https://product.kyobobook.co.kr/detail/S000001057642)

### 정의
부트스트랩이란 네티로 작성한 네트워크 애플리케이션의 동작 방식과 환경을 설정하는 도우미 클래스이다.

### 구조
부트스트랩은 전송 계층, 이벤트 루프, 채널 파이프라인 설정, 소켓 주소와 포트, 소켓 옵션을 설정할 수 있다.

네티 부트스트랩은 서버 애플리케이션을 위한 ServerBootstrap과 클라이언트 애플리케이션을 위한 Bootstrap 클래스로 나뉜다. 

여기에서 말하는 서버 애플리케이션과 클라이언트 애플리케이션의 구분은 소켓 요청을 하는지, 아니면 대기하는지에 따른 구분이다.

상대방 소켓으로 연결을 요청을 시도하는 쪽이 클라이언트이다. ServerBootstrap 클래스는 클라이언트 접속을 대기할 포트를 설정하는 메서드가 추가되어있을 뿐, API 구조는 Bootstrap과 같다.


### ServerBootstrap
```java
public class BootStrap {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                        }
                    });
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
```

여기에서 bossGroup은 클라이언트 연결을 수락하는 부모 스레드 그룹이며 workerGroup은 연결된 클라이언트의 소켓으로부터 데이터 입출력 및 이벤트 처리를 담당하는 자식 스레드 그룹이다.

각 스레드 그룹은 NioEventLoopGroup 클래스의 객체를 설정하였다. NioEventLoopGroup클래스 생성자의 인수로 사용된 숫자는 스레드 그룹 내에서 생성할 최대 스레드 수를 의미한다.

NioEventLoopGroup에서 인수가 없는 생성자를 호출 할 경우 사용할 스레드 수는 하드웨어가 가지고 있는 CPU 코어의 수의 2배를 사용한다.

만약 4코어이고, 하이퍼 스레드를 지원한다면 16개의 스레드가 생성된다.

```java
public class BootStrap {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new OioEventLoopGroup(1);
        EventLoopGroup workerGroup = new OioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(OioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                        }
                    });
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
```
위 코드는 Blocking 모드에서 동작하는 ServerBootstrap이다. 많은 변경을 하지 않고도, Blocking모드로 변경할 수 있다.

```java
public class BootStrap {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new EpollEventLoopGroup(1);
        EventLoopGroup workerGroup = new EpollEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                        }
                    });
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
```
Epoll 입출력 모드를 지원하는 서버 애플리케이션으로 변경한 코드이다. 이 코드도 몇 줄 변경하지 않고도 쉽게 바꿀 수 있다는 것을 알 수 있다. 이 코드는 윈도우 운영체제에서 실행한다면, 오류 코드가 출력되면서 서버가 실행되지 않는다. (Epoll은 Linux에서 지원하는 멀티플럭스 I/O이다.)

### ServerBootstrap API
**group - 이벤트 루프 설정**
데이터 송수신 처리를 위한 이벤트 루프를 설정하는 group메서드를 살펴보자.
```java
@SuppressWarnings('unchecked')
public B group(EventLoopGroup group) {
    if (group == null) {
        throw new NullPointerException("group");
    }
    if (this.group != null) {
        throw new IllegalStateException("group set already");
    }
    this.group == group;
    return (B) this;
}
```
부트스트랩은 ServerBootstrap 클래스와 Bootstrap 클래스로 나뉜다. 

클라이언트는 연결 요청이 완료된 이후의 데이터 송수신 처리를 위해서 하나의 이벤트 루프로 모든 처리가 가능하다.

하지만 서버는 클라이언트의 연결 요청을 수락하기 휘한 이벤트 루프와 데이터 송수신 처리를 위한 이벤트 루프 두 종류의 이벤트 루프가 필요하다.

즉 AbstractBootstrap 클래스에 정의된 grop메서드는 하나의 이벤트 루프만 설정하도록 되어 있다.

ServerBootstrap에서는 두 개의 이벤트 루프를 정의하도록 Override되어있다.

**channel - 소켓 입출력 모드 설정**
소켓의 입출력 모드를 설정하는 channel 메서드의 경우 ServerBootstrap과 Bootstrap클래스 모두 존재한다.

부트스트랩의 channel메서드에 등록된 소켓 채널 생성 클래스가 소켓 채널을 생성한다.

**channelFactory - 소켓 입출력 모드 설정**
소켓의 입출력 모드를 ㅅ ㅓㄹ정하는 API인 channelFactory 메서드는 channel메서드와 동일하게 소켓의 입출력 모드를 설정하는API다.

**handler - 서버 소켓 채널의 이벤트 핸들러 설정**
서버 소켓 채널의 이벤트를 처리할 핸들러 설정 API인 handler 메서드는 등록된 이벤트 핸들러는 서버 소켓 채널에서 발생하는 이벤트를 수신하여 처리한다.

```java
public class BootStrap {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new EpollEventLoopGroup(1);
        EventLoopGroup workerGroup = new EpollEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                        }
                    });
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
```

위 handler에 등록된 LoggingHandler는 네티가 기본적으로 제공하는 코덱이다. 채널에 발생하는 모든 이벤트를 로그로 출력한다.

LoggingHandler는 네티가 제공하는 ChannelDuplexHandler를 상속받고 있다. 

위 코드를 따라가보면, ChannelInboundHandler와 ChannelOutboundHandler인터페이스를 상속받아 구현하고 있다.

양방향 이벤트 모두를 로그로 출력해야하지만, 서버 간의 데이터 송수신 이벤트를 모두 출력하지는 않는다.

이는 ServerBootstrap의 handler에 등록된 이벤트 핸들러는 서버 소켓 채널에서 발생한 이벤트만을 처리하기 때문이다. 


**childHandler - 소켓 채널의 데이터 가공 핸들러 설정**
클라이언트 소켓 채널로 송수신되는 데이터를 가공하는 데이터 핸들러 설정 API이다.

```java
public class BootStrap {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new EpollEventLoopGroup(1);
        EventLoopGroup workerGroup = new EpollEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LoggingHandler(LogLevel.DEBUG));
                            p.addLast(new HttpServerCodec());
                        }
                    });
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
```

**option - 서버 소켓 채널의 소캣 옵션 설정**
서버 소켓 채널의 소켓 옵션을 설정하는 API이다. 여기에서 소켓 옵션이란, 소켓의 동작 방식을 지정하는 것을 말한다.

즉 애플리케이션의 값을 변경하는 것이 아니라 커널에서 사용되는 값을 변경한다는 의미이다.

|옵션|설명|기본값|
|---|---|---|
|TCP_NODELAY|데이터 송수신에 Nagle 알고리즘 비활성화 여부를 지정|false|
|SO_KEEPALIVE|운영체제에서 시정된 시간에 한번씩 keepalve 패킷을 상대방에 전송|false|
|SO_SNDBUF|상대방으로 송신할 커널 송신 버퍼의 크기|커널 설정에 따라 다름|
|SO_RCVBUF|상대방으로부터 수신할 커널 수신 버퍼의 크기|커널 설정에 따라 다름|
|SO_REUSEADDR|TIME_WAIT상태의 포트를 서버 소켓에 바인드할 수 있게함|false|
|SO_LINGER|소켓을 닫을 때 커널의 송신 버퍼에 전송되지 않는 데이터의 전송 대기시간을 지정|false|
|so_BACKLOG|동시에 수용 가능한 소켓 연결 요청 수|_|

```java
public class BootStrap {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new EpollEventLoopGroup(1);
        EventLoopGroup workerGroup = new EpollEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                        }
                    });
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
```
**childOption - 소켓 채널의 소캣 옵션 설정**
childOption메서드는 option 메서드와 같이 소켓 채널에 소켓 옵션을 설정한다.

option은 서버 소켓 채널의 옵션을 설정하는 데 반해 childOption메서드는 서버에 접속한 클라이언트 소켓 채널에 대한 옵션을 설정하는 데 사용한다.

### Bootstrap
클라이언트 애플리케이션을 설정하는 Bootstrap의 주요 API를 살펴보자.

Bootstrap이 제공하는 API는 기본적으로 ServerBootstrap과 같지만, 단일 소켓 채널에 대한 설정이므로 부모와 자식이라는 관계에 해당하는 API는 없다.

**group - 이벤트 루프 설정**
ServerBootstrap과 마찬가지로 소켓 채널의 이벤트 처리를 위한 이벤트 루프 객체를 설정한다.

ServerBootstrap과는 다르게 단 하나의 이벤트 루프만 설정할 수 있다.

**channel - 소켓 입출력 모드 설정**
channel 메서드는 클라이언트 소켓 채널의 입출력 모드를 설정한다.

**channelFactory - 소켓 입출력 모드 설정**
클라이언트 소켓 채널의 입출력 모드를 설정하는 ServerBootstrap의 channelFactory 메서드와 동일한 동작을 수행한다.

**handler - 클라이언트 소켓 채널의 이벤트 핸들러 설정**
이 메서드를 통해서 등록되는 이벤트 핸들러는 클라이언트 소켓 채널에서 발생하는 이벤트를 수신하여 처리한다.

**option - 소켓 채널의 소켓 옵션 설정**
option 메서드는 클라이언트 소켓 채널의 옵션을 설정한다.