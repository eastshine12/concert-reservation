# 콘서트 예매 서비스

## Milestone
![hhplus-gantt.drawio.png](src/main/resources/images/hhplus-gantt.drawio.png)

<br/>

## 문서

- [시퀀스 다이어그램](docs/sequence_diagram.md)
- [ERD](docs/erd.md)
- [API 명세서](docs/api_specification.md)

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


<br/><br/>