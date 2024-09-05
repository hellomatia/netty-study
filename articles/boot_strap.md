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



