## API 명세서

<br/>

### 1. **유저 토큰 발급 API**

- **설명**: 유저가 콘서트 예약 대기열 진입에 대한 토큰을 발급받는 API
- **HTTP Method**: `POST`
- **URL**: `/api/token`

**Request**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| concertId | Integer | 콘서트 ID |
| concertScheduleId | Integer | 콘서트 일정 ID |
| userId | Integer | 유저 ID |

**Request 예시**

```json
POST /api/token
Content-Type: application/json;charset=UTF-8
{
    "concertId": 2,
    "concertScheduleId": 5,
    "userId": 1
}
```

**Response**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| token | String | 토큰 (UUID) |
| status | String | 응답 상태 ("issued", "existing") |

**Response Body 예시**

```json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "status": "issued"
}
```

**에러 코드**

| HTTP 상태 코드 | 설명 |
| --- | --- |
| 400 | 요청 값이 잘못된 경우 (필수 필드 누락 또는 형식 오류) |
| 401 | 인증 실패 (유효하지 않은 유저 ID) |
| 404 | 콘서트 또는 콘서트 일정 ID가 존재하지 않는 경우 |
| 500 | 서버 내부 오류 |

<br/><br/><br/>

---

### 2. **대기열 조회 API**

- **설명**: 유저가 자신의 대기열 상태를 조회하는 API
- **HTTP Method**: `GET`
- **URL**: `/api/queue`

**Request Header**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| token | String | 유저 대기열 토큰 |

**Request 예시**

```json
GET /api/queue
Queue-Token: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| status | String | 현재 대기열 상태 ("waiting", "active") |
| queuePosition | String | 대기 중일 때, 현재 대기열 순번 |

**Response Body 예시**

```json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
{
    "status": "waiting",
    "queuePosition": 10
}
```

**에러 코드**

| HTTP 상태 코드 | 설명 |
| --- | --- |
| 400 | 요청 값이 잘못된 경우 (유효하지 않은 토큰 형식) |
| 404 | 대기열에 해당 유저가 존재하지 않는 경우 |
| 500 | 서버 내부 오류 |

<br/><br/><br/>

---

### 3. **예약 가능 날짜 조회 API**

- **설명**: 유저가 특정 콘서트에 대해 예약 가능한 날짜 목록을 조회하는 API
- **HTTP Method**: `GET`
- **URL**: `/api/concert/{concertId}/available`

**Request Header**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Queue-Token | String | 유저 대기열 토큰 |

**Path Parameters**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| concertId | Integer | 콘서트 ID |

**Request 예시**

```json
GET /api/concert/2/available
Queue-Token: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| dates | List | 예약 가능한 날짜 목록 |

**Response Body 예시**

```json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
{
    "dates": [
        "2024-10-05T19:00:00",
        "2024-10-06T19:00:00",
        "2024-10-07T19:00:00"
    ]
}
```

**에러 코드**

| HTTP 상태 코드 | 설명 |
| --- | --- |
| 400 | 요청 값이 잘못된 경우 (유효하지 않은 토큰 형식) |
| 404 | 콘서트가 존재하지 않는 경우 |
| 500 | 서버 내부 오류 |

<br/><br/><br/>

---

### 4. **예약 가능 날짜에 대한 좌석 조회 API**

- **설명**: 유저가 특정 콘서트의 예약 가능한 해당 날짜에 대해 예약 가능한 좌석 목록을 조회하는 API
- **HTTP Method**: `GET`
- **URL**: `/api/concert/{concertId}/schedule/{scheduleId}/available`

**Request Header**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Queue-Token | String | 유저 대기열 토큰 |

**Path Parameters**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| concertId | Integer | 콘서트 ID |
| scheduleId | Integer | 콘서트 일정 ID |

**Request 예시**

```json
GET /api/concert/2/schedule/3/available
Queue-Token: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| seats | List | 예약 가능한 좌석 목록 |

**Response Body 예시**

```json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
{
    "seats": [
        {"seat_number": "A1", "status": "available"},
        {"seat_number": "A2", "status": "available"},
        {"seat_number": "A3", "status": "reserved"}
    ]
}
```

**에러 코드**

| HTTP 상태 코드 | 설명 |
| --- | --- |
| 400 | 요청 값이 잘못된 경우 (유효하지 않은 토큰 형식) |
| 404 | 콘서트 or 콘서트 일정이 존재하지 않는 경우 |
| 500 | 서버 내부 오류 |

<br/><br/><br/>

---

### 5. **좌석 예약 요청 API**

- **설명**: 유저가 콘서트의 예약 가능한 좌석을 예약하는 API
- **HTTP Method**: `POST`
- **URL**: `/api/concert/{concertId}/schedule/{scheduleId}/reserve`

**Request Header**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Queue-Token | String | 유저 대기열 토큰 |

**Request Body**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| seatId | Integer | 예약할 좌석 ID |

**Path Parameters**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| concertId | Integer | 콘서트 ID |
| scheduleId | Integer | 콘서트 일정 ID |

**Request 예시**

```json
POST /api/concert/2/schedule/3/reserve
Queue-Token: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json;charset=UTF-8
{
    "seatId": 7
}
```

**Response**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| status | String | 요청 처리 결과 (예: "success", "invalid_token", "invalid_concert", "invalid_date", "seat_already_reserved") |

**Response Body 예시**

```json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
{
    "status": "success"
}
```

**에러 코드**

| HTTP 상태 코드 | 설명 |
| --- | --- |
| 400 | 요청 값이 잘못된 경우 (유효하지 않은 토큰 형식) |
| 404 | 콘서트 or 콘서트 일정 or 좌석이 존재하지 않는 경우 |
| 500 | 서버 내부 오류 |

<br/><br/><br/>

---

### 6. **잔액 충전 API**

- **설명**: 유저가 잔액을 충전하는 API
- **HTTP Method**: `POST`
- **URL**: `/api/user/{userId}/charge`

**Request Header**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Queue-Token | String | 유저 대기열 토큰 |

**Request Body**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| amount | Integer | 충전할 금액 |

**Path Parameters**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| userId | Integer | 유저 ID |

**Request 예시**

```json
POST /api/user/1/charge
Queue-Token: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json;charset=UTF-8
{
    "amount": 10000
}
```

**Response**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| status | String | 요청 처리 결과 (예: "success", “failed”) |

**Response Body 예시**

```json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
{
    "status": "success"
}
```

**에러 코드**

| HTTP 상태 코드 | 설명 |
| --- | --- |
| 400 | 요청 값이 잘못된 경우 (유효하지 않은 토큰 형식 또는 충전 금액) |
| 404 | 유저가 존재하지 않는 경우 |
| 500 | 서버 내부 오류 |

<br/><br/><br/>

---

### 7. **잔액 조회 API**

- **설명**: 유저의 현재 잔액을 조회하는 API
- **HTTP Method**: `GET`
- **URL**: `/api/user/{userId}/balance`

**Request Header**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Queue-Token | String | 유저 대기열 토큰 |

**Path Parameters**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| userId | Integer | 유저 ID |

**Request 예시**

```json
GET /api/user/1/balance
Queue-Token: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| balance | Integer | 잔액 |

**Response Body 예시**

```json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
{
    "balance": 50000
}
```

**에러 코드**

| HTTP 상태 코드 | 설명 |
| --- | --- |
| 400 | 요청 값이 잘못된 경우 (유효하지 않은 토큰 형식) |
| 404 | 유저가 존재하지 않는 경우 |
| 500 | 서버 내부 오류 |

<br/><br/><br/>

---

### 8. **결제 API**

- **설명**: 유저가 예약한 좌석에 대해 결제를 진행하는 API
- **HTTP Method**: `POST`
- **URL**: `/api/payment`

**Request Header**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Queue-Token | String | 유저 대기열 토큰 |

**Request Body**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| userId | Integer | 유저 ID |
| amount | Integer | 결제 금액 |

**Request 예시**

```json
POST /api/payment
Queue-Token: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json;charset=UTF-8
{
    "user_id": 1,
    "amount": 10000
}
```

**Response**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| status | Integer | 결제 처리 결과 (예: "success", “payment_failed”, “insufficient_balance”) |

**Response Body 예시**

```json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
{
    "status": "success"
}
```

**에러 코드**

| HTTP 상태 코드 | 설명 |
| --- | --- |
| 400 | 요청 값이 잘못된 경우 (유효하지 않은 토큰 형식) |
| 404 | 유저가 존재하지 않는 경우 |
| 500 | 서버 내부 오류 |

<br/><br/><br/>

---

### 9. **결제 내역 조회 API**

- **설명**: 유저의 결제 내역을 조회하는 API
- **HTTP Method**: `GET`
- **URL**: `/api/user/{user_id}/payments`

**Path Parameters**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| userId | Integer | 유저 ID |

**Request 예시**

```json
GET /api/users/1/payments
```

**Response**

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| id | Integer | 결제 ID |
| title | String | 콘서트 이름 |
| time | String | 콘서트 일시 |
| seatNumber | String | 콘서트 예약 좌석 번호 |
| price | Integer | 콘서트 티켓 가격 |
| status | String | 결제 상태 ("success", “canceled”) |

**Response Body 예시**

```json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
{
  "payments": [
    {
      "id": 3,
      "title": "BTS 월드 투어",
      "time": "2024-10-05T19:00:00",
      "seatNumber": "A1",
      "price": 110000,
      "status": "success"
    },
    {
      "id": 4,
      "title": "IU 상암 콘서트",
      "time": "2024-10-10T19:00:00",
      "seatNumber": "B16",
      "price": 90000,
      "status": "success"
    }
  ]
}  
```

**에러 코드**

| HTTP 상태 코드 | 설명 |
| --- | --- |
| 404 | 유저가 존재하지 않는 경우 |
| 500 | 서버 내부 오류 |

<br/><br/><br/>
