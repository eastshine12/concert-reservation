## 시퀀스 다이어그램

### 1. 유저 토큰 발급
사용자가 콘서트 예매에 대한 대기열 토큰을 발급받기 위해 필요한 과정을 정의했습니다.
유저, 콘서트, 콘서트 일정의 유효성을 검증한 후, 대기열에 있는지 확인하여 이미 존재하는 경우에는 기존 토큰을 반환하고, 새로운 경우에는 새로 발급하여 제공합니다.

```mermaid
sequenceDiagram
    participant User as User
    participant API as API
    participant DB as Database
    participant Queue as Queue Manager
    participant Token as Token Manager

    User->>API: 토큰 발급 요청

    API->>DB: 유저 ID 존재 여부 확인
    DB-->>API: 유저 ID 존재 확인
    
    alt 유저 ID 없음
        API-->>User: 에러 응답 (status: "invalid_user")
    end

    API->>DB: 콘서트 ID 존재 여부 확인
    DB-->>API: 콘서트 ID 존재 확인
    
    alt 콘서트 ID 없음
        API-->>User: 에러 응답 (status: "invalid_concert")
    end

    API->>DB: 해당 콘서트 일정에 콘서트 진행 여부 확인
    DB-->>API: 콘서트 일정 진행 확인
    
    alt 해당 일정에 진행하는 콘서트 없음
        API-->>User: 에러 응답 (status: "invalid_datetime")
    end
    
    API->>Queue: 대기열 조회 요청 (유저 ID, 콘서트 ID, 날짜)
    alt 이미 대기열에 있음
        Queue-->>API: 기존 토큰 반환
        API-->>User: 기존 토큰 반환 (status: "existing")
    else 대기열에 없음
        API->>Token: 새로운 토큰 발급 요청 (유저 ID, 콘서트 ID, 날짜)
        Token->>DB: 새로운 토큰 발급 및 저장
        DB-->>Token: 저장 완료
        Token-->>API: 새 토큰 반환
        API-->>User: 새 토큰 반환 (status: "issued")
    end

```

<br/><br/><br/>

### 2. 대기열 조회
사용자가 대기열의 상태를 주기적으로 확인하는 과정을 정의했습니다.
대기열에서의 현재 위치와 활성화 여부를 확인하고, 대기열에 존재하지 않는 경우에는 에러 응답을 반환합니다.

```mermaid
sequenceDiagram
    participant User as User
    participant API as API
    participant QueueMgr as Queue Manager

    loop 일정 시간마다
        User->>API: 대기열 상태 확인 요청 (유저 토큰)
        
        API->>QueueMgr: 유저 대기열 상태 조회 요청 (유저 토큰)
        
        alt 대기열에 존재하지 않음
            QueueMgr-->>API: 대기열 상태 없음
            API-->>User:  응답 (status: "not_found")
        end
        
        QueueMgr-->>API: 대기열 상태 반환 (현재 위치, 활성화 여부)

        alt 대기 중
            API-->>User: 대기열 상태 반환 (status: "waiting", 현재 위치)
        else 활성화 상태
            API-->>User: 활성화 상태 (status: "active")
        end
    end

```

<br/><br/><br/>

### 3. 예약 가능 날짜 조회

사용자가 특정 콘서트의 예약 가능 날짜를 조회하는 과정을 정의했습니다.
유효한 토큰과 콘서트 ID를 검증한 후, 데이터베이스에서 예약 가능한 날짜 목록을 반환합니다.

```mermaid
sequenceDiagram
    participant User as User
    participant API as API
    participant TokenMgr as Token Manager
    participant DB as Database

    User->>API: 예약 가능 날짜 조회 요청 (콘서트 ID, 유저 토큰)

    API->>TokenMgr: 유저 토큰 검증 (토큰 유효성 및 활성화 여부 확인)
    TokenMgr-->>API: 유저 토큰 유효성 결과 반환
    
    alt 유효하지 않은 토큰
        API-->>User: 에러 응답 (status: "invalid_token")
    end

    API->>DB: 콘서트 ID 존재 여부 확인
    DB-->>API: 콘서트 ID 존재 확인
    
    alt 콘서트 ID 없음
        API-->>User: 에러 응답 (status: "invalid_concert")
    end

    API->>DB: 콘서트 ID로 예약 가능 날짜 조회
    DB-->>API: 예약 가능한 날짜 목록 반환
    API-->>User: 예약 가능 날짜 목록 반환

```

<br/><br/><br/>

### 4. 예약 가능 해당 날짜에 대한 좌석 조회

특정 콘서트의 예약 가능한 날짜에 대한 좌석 정보를 조회하는 과정을 정의했습니다.
유저의 토큰과 콘서트 ID의 유효성을 확인한 후, 해당 날짜에 대한 좌석 상태를 반환합니다.

```mermaid
sequenceDiagram
    participant User as User
    participant API as API
    participant TokenMgr as Token Manager
    participant DB as Database

    User->>API: 좌석 조회 요청 (콘서트 ID, 예약 가능 날짜, 유저 토큰)

    API->>TokenMgr: 유저 토큰 검증 (토큰 유효성 및 활성화 여부 확인)
    TokenMgr-->>API: 유저 토큰 유효성 결과 반환
    
    alt 유효하지 않은 토큰
        API-->>User: 에러 응답 (status: "invalid_token")
    end

    API->>DB: 콘서트 ID 존재 여부 확인
    DB-->>API: 콘서트 ID 존재 확인
    
    alt 콘서트 ID 없음
        API-->>User: 에러 응답 (status: "invalid_concert")
    end

    API->>DB: 예약 가능한 해당 날짜에 대한 콘서트 일정 ID 여부 확인
    DB-->>API: 날짜에 해당하는 콘서트 일정 ID 존재 확인
    
    alt 콘서트가 해당 날짜에 없음
        API-->>User: 에러 응답 (status: "invalid_date")
    end
    
    API->>DB: 좌석 정보 조회 (콘서트 ID, 콘서트일정 ID)
    DB-->>API: 해당 콘서트 일정의 모든 좌석에 대한 좌석번호, 상태값 반환
    API-->>User: 좌석번호, 상태값 반환

```

<br/><br/><br/>

### 5. 좌석 예약 요청

사용자가 좌석을 예약하는 과정을 정의했습니다.
유저의 토큰 검증 후 콘서트와 좌석의 유효성을 확인하고, 좌석이 예약 가능할 경우 예약을 진행합니다.

```mermaid
sequenceDiagram
    participant User as User
    participant API as API
    participant TokenMgr as Token Manager
    participant DB as Database

    User->>API: 좌석 예약 요청 (콘서트 ID, 예약 가능 날짜, 유저 토큰, 좌석 정보)
    
    API->>TokenMgr: 유저 토큰 검증 (유저 토큰)
    TokenMgr-->>API: 토큰 유효성 결과 반환
    
    alt 유효하지 않은 토큰
        API-->>User: 에러 응답 (status: "invalid_token")
    end

    API->>DB: 콘서트 ID 존재 여부 확인
    DB-->>API: 콘서트 ID 존재 확인
    
    alt 콘서트 ID 없음
        API-->>User: 에러 응답 (status: "invalid_concert")
    end
    
    API->>DB: 예약 가능 날짜에 대한 콘서트 진행 여부 확인
    DB-->>API: 날짜에 해당하는 콘서트 확인
    
    alt 콘서트가 해당 날짜에 없음
        API-->>User: 에러 응답 (status: "invalid_date")
    end

    API->>DB: 좌석 정보 조회 (콘서트 ID, 날짜, 좌석 식별자)
    DB-->>API: 좌석 정보 반환

    alt 선택한 좌석이 예약 가능
        API->>DB: 좌석 예약 요청 (유저 ID, 콘서트 ID, 좌석 정보)
        DB-->>API: 임시 배정 및 예약 완료 확인
        API-->>User: 예약 완료 응답 (status: "success")
    else 선택한 좌석이 이미 예약됨
        API-->>User: 에러 응답 (status: "seat_already_reserved")
    end

```

<br/><br/><br/>

### 6. 잔액 충전

사용자가 잔액을 충전하는 과정을 정의했습니다.
토큰 검증 후 유저의 현재 잔액을 확인하고, 충전 금액의 유효성을 검증한 뒤 잔액을 업데이트합니다.

```mermaid
sequenceDiagram
    participant User as User
    participant API as API
    participant TokenMgr as Token Manager
    participant DB as Database

    User->>API: 잔액 충전 요청 (유저 토큰, 유저 ID, 충전 금액)

    API->>TokenMgr: 유저 토큰 검증 (유저 토큰)
    TokenMgr-->>API: 토큰 유효성 결과 반환

    alt 유효하지 않은 토큰
        API-->>User: 에러 응답 (status: "invalid_token")
    end

    API->>DB: 유저 조회 (유저 ID)
    DB-->>API: 유저 반환

    API->>DB: 현재 잔액 조회 (유저 ID)
    DB-->>API: 현재 잔액 반환

    API->>DB: 충전 금액 유효성 검사 (충전 금액)
    alt 충전 금액 유효하지 않음
        API-->>User: 에러 응답 (status: "invalid_amount")
    end

    API->>DB: 잔액 업데이트 (유저 ID, 충전 금액)
    DB-->>API: 잔액 업데이트 완료

    API-->>User: 충전 완료 응답 (status: "success")

```

<br/><br/><br/>

### 7. 잔액 조회

사용자가 잔액을 조회하는 과정을 정의했습니다.
토큰 검증 후 유저에 대한 유효성을 확인하고, 유저의 잔액 정보를 반환합니다.

```mermaid
sequenceDiagram
    participant User as User
    participant API as API
    participant TokenMgr as Token Manager
    participant DB as Database

    User->>API: 잔액 조회 요청 (유저 토큰, 유저 ID)

    API->>TokenMgr: 유저 토큰 검증 (유저 토큰)
    TokenMgr-->>API: 토큰 유효성 결과 반환

    alt 유효하지 않은 토큰
        API-->>User: 에러 응답 (status: "invalid_token")
    end

    API->>DB: 유저 조회 (유저 ID)
    DB-->>API: 유저 반환

    API->>DB: 잔액 조회 (유저 ID)
    DB-->>API: 현재 잔액 반환

    API-->>User: 잔액 정보 반환 (현재 잔액)

```

<br/><br/><br/>

### 8. 결제

사용자가 예약한 좌석에 대한 결제를 진행하는 기능을 정의했습니다.
토큰 검증 후 유저와 현재 잔액, 결제 금액에 대한 유효성을 검사하고 결제를 요청합니다.
결제에 실패한 경우 임시 좌석에 대한 예약을 해제하도록 요청하고, 결제에 성공한 경우 잔액, 좌석 상태, 예약 상태, 토큰 상태를 업데이트하고 결제 내역을 저장합니다.

```mermaid
sequenceDiagram
    participant User as User
    participant API as API
    participant TokenMgr as Token Manager
    participant DB as Database
    participant Payment as Payment

    User->>API: 결제 요청 (유저 토큰, 유저 ID, 결제 금액)

    API->>TokenMgr: 유저 토큰 검증 (유저 토큰)
    TokenMgr-->>API: 토큰 유효성 결과 반환

    alt 유효하지 않은 토큰
        API-->>User: 에러 응답 (status: "invalid_token")
    end

    API->>DB: 유저 조회 (유저 ID)
    DB-->>API: 유저 반환

    API->>DB: 현재 잔액 조회 (유저 ID)
    DB-->>API: 현재 잔액 반환

    API->>DB: 결제 금액 유효성 검사 (결제 금액)
    alt 잔액 부족
        API-->>User: 에러 응답 (status: "insufficient_balance")
    end

    API->>Payment: 결제 요청 (결제 금액, 결제 정보)
    Payment-->>API: 결제 성공 여부 반환

    alt 결제 실패
        API->>DB: 임시 좌석 예약 해제 요청 (유저 ID)
        DB-->>API: 임시 좌석 예약 해제 완료
        API-->>User: 에러 응답 (status: "payment_failed")
    end

    API->>DB: 잔액 업데이트 (유저 ID, 결제 금액)
    DB-->>API: 잔액 업데이트 완료

    API->>DB: 좌석 예약 확정 상태로 변경 (유저 ID)
    DB-->>API: 좌석 상태 업데이트 완료

    API->>DB: 결제 내역 저장 (유저 ID, 결제 금액, 결제 정보)
    DB-->>API: 결제 내역 저장 완료

    API->>TokenMgr: 토큰 만료 요청 (유저 토큰)
    TokenMgr-->>API: 토큰 만료 완료

    API-->>User: 결제 완료 응답 (status: "success")

```

<br/><br/><br/>


### 9. 결제 내역 조회

사용자가 과거의 결제 내역을 조회할 수 있도록 하는 기능을 정의했습니다.
유저에 대한 유효성을 검사하고 결제 내역을 반환합니다.

```mermaid
sequenceDiagram
    participant User as User
    participant API as API
    participant DB as Database

    User->>API: 결제 내역 조회 요청 (유저 토큰, 유저 ID)

    API->>DB: 유저 조회 (유저 ID)
    DB-->>API: 유저 반환

    API->>DB: 결제 내역 조회 (유저 ID)
    DB-->>API: 결제 내역 반환

    API-->>User: 결제 내역 정보 반환

```

<br/><br/><br/>
