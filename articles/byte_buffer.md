# 바이트 버퍼

[자바 네트워크 소녀 Netty](https://product.kyobobook.co.kr/detail/S000001057642)

자바 버퍼는 바이트 데이터를 저장하는 저장소로써 저장된 데이터를 읽고 쓰는 메서드를 제공한다.

저장되는 데이터 형에 따라서 읽고 쓰는 데이터의 크기가 달라지는데 이를 Buffer 추상 클래스를 사용하여 처리한다.

## 자바 NIO 바이트 버퍼
자바 NIO 바이트 버퍼는 바이트 데이터를 저장하고 읽는 저장소이다. 배열을 멤버 변수로 가지고 있으며 배열에 대한 읽고 쓰기를 추상화한 메서드를 제공한다.

자바에서 제공하는 버퍼로는 ByteBuffer, CharBuffer, IntBuffer, ShortBuffer, LongBuffer, FloatBuffer, DoubleBuffer가 있다

바이트 버퍼 클래스는 내부의 배열 상태를 관리하는 세가지 속성을 가지고 있다.

**capacity**
- 버퍼에 저장할 수 있는 데이터의 크기로 한 번 정하며 변경이 불가능하다.

**position**
- 읽기 또는 쓰기가 작업 중인 위치를 나타낸다.

**limit**
- 읽고 쓸 수 있는 버퍼 공간의 최대치를 나타낸다.

### 자바 바이트 버퍼 생성
통상적으로 자바에서 객체를 생성할 때는 생성자를 사용하지만, 자바에서는 데이터형에 따를 추상 클래스의 팩토리 메서드를 통해서 생성한다.
바이트 버퍼 를 생성하는 메서드도 3가지 존재한다.

**allocate**
- JVM의 힙 영역에 바이트 버퍼를 생성한다.

**allocateDirect**
- JVM의 힙 영역이 아닌 운영체제의 커널 영역에 바이트 버퍼를 생성한다.

**wrap**
- 입력된 바이트 배열을 사용하여 바이트 버퍼를 생성한다.

일반적으로 allocate 메서드를 사용하여 생성한 버퍼를 힙 버퍼, allocateDirect를 사용하여 생성된 버퍼를 다이렉트 버퍼라 부른다.

다이렉트 버퍼는 생성 시간은 길지만 더 빠른 읽기와 쓰기를 제공한다.

### 버퍼 사용
자바 바이트 버퍼는 이전에 수행한 put또는 get 메서드가 호출된 이후의 position 정보를 저장하기 위해 filp 메서드를 제공한다.

자바의 바이터 버퍼를 사용 할때는 읽기와 쓰기를 분리하여 생각해야 하고, 특히 다중 스레드 환경에서 바이터 버퍼를 공유하지 않아야 한다.

## 네티 바이트 버퍼

네티 바이트 버퍼는 자바 바이트 버퍼에 비하여 더 빠른 성능을 제공한다. 

네티 바이트 버퍼 버퍼풀은 빈번한 바이트 버퍼 할당과 해제에 대한 부담을 줄여주어 가비지 컬렉션에 대한 부담을 줄여준다.

- 별도의 읽기 인덱스와 쓰기 인덱스
- filp 메서드 없이 읽기 가능
- 가변 바이트 버퍼
- 바이트 버퍼 풀
- 복합 버퍼
- 자바의 바이트 버퍼와 네티의 바이트 버퍼 상호 변환

네티 바이트 버퍼는 저장되는 데이터형에 따른 별도의 바이트 버퍼를 사용하지 않은 대신 각 데이터형에 따른 읽기 쓰기 메서드를 제공한다.

일기 쓰기 메서드는 readFloat, writeFloat과 같이 행동을 의미하는 read/write 접두사와 데이터형을 의마하는 접미사를 사용한다.

읽기 인덱스와 쓰기 인덱스를 호출할 때마다 인덱스를 증가한다.

네티의 바이트 버퍼는 읽기 작업이 완료된 후에 쓰기 작업을 위해서 flip 메서드를 호출이 필요 없다.