# 콘서트 예약 서비스

## Milestone
![hhplus-gantt.drawio.png](src/main/resources/images/hhplus-gantt.drawio.png)

<br/>

## 문서

- [시퀀스 다이어그램](docs/sequence_diagram.md)
- [ERD](docs/erd.md)
- [API 명세서](docs/api_specification.md)
- [Chapter 2 중간 회고록](https://eastshine12.tistory.com/67)
- [동시성 제어 방식 비교 및 성능 테스트](https://eastshine12.tistory.com/68)
- [Redis 기반의 캐싱 및 대기열 관리를 통한 콘서트 예약 서비스 성능 개선](https://eastshine12.tistory.com/69)
- [서비스 확장을 위한 트랜잭션 분리와 이벤트 기반 설계](https://eastshine12.tistory.com/71)
- [콘서트 예약 서비스의 인덱스 설계와 성능 비교](https://eastshine12.tistory.com/70)

<br/>

## 패키지 구조

```
hhplus
    └── concertreservation
        ├── interfaces       # 외부 인터페이스 (Controller, API 등)
        │   └── api          # 도메인별 API
        │       ├── concert  # 콘서트 관련 API 및 사용자 요청 처리 (Controller)
        │       ├── payment  # 결제 관련 API (Controller)
        │       ├── queue    # 유저 대기열 관련 API (Controller)
        │       ├── token    # 토큰 관련 API (Controller)
        │       └── user     # 사용자 관련 API 및 인증, 등록 처리 (Controller)
        │   
        ├── application      # 비즈니스 로직을 처리하는 유스케이스(Use Case) 계층
        │   ├── concert      # 콘서트 비즈니스 로직 처리 (Facade)
        │   ├── payment      # 결제 로직 처리 (Facade)               
        │   ├── tokenQueue   # 토큰 대기열 관련 로직 처리 (Facade)
        │   └── user         # 사용자 관련 비즈니스 로직 처리 (Facade)
        │   
        ├── domain           # 핵심 비즈니스 로직
        │   ├── concert      # 콘서트 관련 도메인 객체 및 비즈니스 규칙 정의 (Service, Entity, Repository)
        │   ├── payment      # 결제 관련 도메인 객체 및 트랜잭션 관리 규칙 정의 (Service, Entity, Repository)
        │   ├── reservation  # 예약 관련 도메인 객체 및 비즈니스 규칙 정의 (Service, Entity, Repository)
        │   ├── tokenQueue   # 토큰 대기열 관련 도메인 객체 및 비즈니스 규칙 정의 (Service, Entity, Repository)
        │   └── user         # 사용자 관련 도메인 객체 및 비즈니스 규칙 정의 (Service, Entity, Repository)
        │   
        └── infrastructure   # 외부 시스템과의 통신 (DB, MQ 등)
            ├── concert      # 콘서트 정보 저장 및 조회 시스템 연동
            ├── payment      # 결제 시스템 연동
            ├── queue        # 대기열 관리를 위한 인프라 연동
            ├── reservation  # 예약 정보 저장 및 처리 관련 인프라 연동
            └── user         # 사용자 정보 저장 및 조회 시스템 연동

```

<br/>

## 기술 스택

- **Language**: Kotlin
- **Framework**: Spring Boot
- **Database**: H2
- **Build Tool**: Gradle
- **Test Framework**: JUnit 5

<br/>

## Swagger API 명세서

### 전체

---

![swagger-full.png](src/main/resources/images/swagger/swagger-full.png)

<br/>

### 상세

---

<details>
<summary>콘서트 API</summary>

![swagger-concert-1.png](src/main/resources/images/swagger/swagger-concert-1.png)
![swagger-concert-2.png](src/main/resources/images/swagger/swagger-concert-2.png)
![swagger-concert-3.png](src/main/resources/images/swagger/swagger-concert-3.png)
![swagger-concert-4.png](src/main/resources/images/swagger/swagger-concert-4.png)
![swagger-concert-5.png](src/main/resources/images/swagger/swagger-concert-5.png)
![swagger-concert-6.png](src/main/resources/images/swagger/swagger-concert-6.png)

</details>

---

<details>
<summary>유저 API</summary>

![swagger-user-1.png](src/main/resources/images/swagger/swagger-user-1.png)
![swagger-user-2.png](src/main/resources/images/swagger/swagger-user-2.png)
![swagger-user-3.png](src/main/resources/images/swagger/swagger-user-3.png)
![swagger-user-4.png](src/main/resources/images/swagger/swagger-user-4.png)

</details>

---

<details>
<summary>대기열 API</summary>

![swagger-queue-1.png](src/main/resources/images/swagger/swagger-queue-1.png)
![swagger-queue-2.png](src/main/resources/images/swagger/swagger-queue-2.png)
![swagger-queue-3.png](src/main/resources/images/swagger/swagger-queue-3.png)
![swagger-queue-4.png](src/main/resources/images/swagger/swagger-queue-4.png)

</details>

---

<details>
<summary>결제 API</summary>

![swagger-payment-1.png](src/main/resources/images/swagger/swagger-payment-1.png)
![swagger-payment-2.png](src/main/resources/images/swagger/swagger-payment-2.png)
![swagger-payment-3.png](src/main/resources/images/swagger/swagger-payment-3.png)
![swagger-payment-4.png](src/main/resources/images/swagger/swagger-payment-4.png)

</details>


<br/><br/>