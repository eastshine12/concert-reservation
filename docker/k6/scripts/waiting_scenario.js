import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';
import exec from 'k6/execution';

const waitingTokenDuration = new Trend('waiting_token_duration_p99');
const queueStatusDuration = new Trend('queue_status_duration_p99');

const CONFIG = {
    baseUrl: 'http://spring-boot-app:8080',
    concertId: 1,
    scheduleId: 1,
};

export let options = {
    // stages: [
    //   { duration: '10s', target: 10 },  // 10초 동안 10 VU로 증가
    //   { duration: '10s', target: 30 },  // 10초 동안 30 VU로 증가
    //   { duration: '10s', target: 50 },  // 10초 동안 50 VU로 증가
    //   { duration: '10s', target: 100 }, // 10초 동안 100 VU로 증가
    //   { duration: '10s', target: 200 }, // 10초 동안 200 VU로 증가
    //   { duration: '10s', target: 0 },   // 10초 동안 0으로 감소
    // ],
    stages: [
        { duration: '10s', target: 100 },  // 10초 동안 100 VU
        { duration: '10s', target: 1000 }, // 10초 동안 1000 VU로 급증
        { duration: '30s', target: 100 },  // 30초 동안 100 VU로 감소
    ],
    thresholds: {
        http_req_duration: ['p(99)<400'], // p99가 400ms 이하
        http_req_failed: ['rate<0.01'],   // 실패율이 1% 이하
    },
};

export default function () {
    // 토큰 발급
    let token = getWaitingToken()

    // 토큰 상태 조회
    for (let i = 0; i < 5; i++) { // 5회 반복
        getQueueStatus(token)
        sleep(1);
    }
}

function getWaitingToken() {
    let headers = {
        'Content-Type': 'application/json',
    };
    let payload = JSON.stringify({
        userId: exec.scenario.iterationInTest + 1,
        concertId: CONFIG.concertId,
        concertScheduleId: CONFIG.scheduleId,
    });
    let res = http.post(`${CONFIG.baseUrl}/api/waiting-queues`, payload, { headers });
    check(res, { 'status is 201': (r) => r.status === 201 });
    waitingTokenDuration.add(res.timings.duration);

    return JSON.parse(res.body).token;
}

function getQueueStatus(token) {
    let headers = {
        'Content-Type': 'application/json',
        'Queue-Token': token,
    };
    let res = http.get(`${CONFIG.baseUrl}/api/waiting-queues`, { headers }, );
    check(res, { 'status is 200': (r) => r.status === 200 });
    queueStatusDuration.add(res.timings.duration);
}