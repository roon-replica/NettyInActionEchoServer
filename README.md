# netty in action ch2 echo server 


### 실행 방법
- echo 
  - 클라이언트에서 보낸 메세지를 다시 그대로 응답하는 서버
  1. EchoServer main 함수 실행 (argument : port번호)
  2. EchoClient main 함수 실행 (argument : host port번호)

- transport
   - 연결을 수락하고 메세지를 보낸 다음 연결을 닫는 서버
  1. Jdk server main 함수 실행
  2. 클라이언트는 echo client 이용하면 됨


### 특징
- 이 단순한 애플리케이션도 수천 개의 동시 연결을 지원할 수 있다고 함..
- 일반 소켓 기반 자바 애플리케이션보다 훨씬 많은 초당 메세지를 처리할 수 있다고 함
