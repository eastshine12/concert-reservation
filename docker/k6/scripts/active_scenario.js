import http from 'k6/http';
import {check, sleep} from 'k6';
import exec from 'k6/execution';

const CONFIG = {
    baseUrl: 'http://spring-boot-app:8080',
    concertId: 1,
    scheduleId: 1,
};

export let options = {
  stages: [
    { duration: '10s', target: 10 },  // 10초 동안 10 VU로 증가
    { duration: '10s', target: 15 },  // 10초 동안 20 VU로 증가
    { duration: '10s', target: 20 },  // 10초 동안 30 VU로 증가
    { duration: '10s', target: 25 }, // 10초 동안 50 VU로 증가
    { duration: '10s', target: 30 }, // 10초 동안 50 VU로 증가
    { duration: '10s', target: 0 },   // 10초 동안 0으로 감소
  ],
  thresholds: {
      http_req_duration: ['p(99)<400'], // p99가 400ms 이하
      http_req_failed: ['rate<0.01'],   // 실패율이 1% 이하
  },
};

export default function () {
    let currentId = exec.scenario.iterationInTest + 1;
    let token = getWaitingToken(currentId);

    getConcertSeats(token);
    sleep(2);
    let reservationId = reserveSeat(token, currentId);
    sleep(2);
    processPayment(token, reservationId, currentId);
    sleep(2);
}

export function teardown(data) {
    console.log('테스트 완료');
}

function getWaitingToken(currentId) {
    let headers = {
        'Content-Type': 'application/json',
    };
    let payload = JSON.stringify({
        userId: currentId,
        concertId: CONFIG.concertId,
        concertScheduleId: CONFIG.scheduleId,
    });
    let res = http.post(`${CONFIG.baseUrl}/api/waiting-queues`, payload, { headers });
    check(res, { 'status is 201': (r) => r.status === 201 });

    return JSON.parse(res.body).token;
}

function getConcertSchedules(token) {
    let headers = {
      'Content-Type': 'application/json',
      'Queue-Token': token,
    };
    let res = http.get(`${CONFIG.baseUrl}/api/concerts/${CONFIG.concertId}`, { headers });
    check(res, { 'status is 200': (r) => r.status === 200 });
}

function getConcertSeats(token) {
    let headers = {
      'Content-Type': 'application/json',
      'Queue-Token': token,
    };
    let res = http.get(
        `${CONFIG.baseUrl}/api/concerts/${CONFIG.concertId}/schedules/${CONFIG.scheduleId}/seats`,
        { headers }
    );
    check(res, { 'status is 200': (r) => r.status === 200 });
}

function reserveSeat(token, currentId) {
    let headers = {
      'Content-Type': 'application/json',
      'Queue-Token': token,
    };
    let payload = JSON.stringify({
        userId: currentId,
        concertId: 1,
        scheduleId: 1,
        seatId: currentId,
    });
    let res = http.post(`${CONFIG.baseUrl}/api/concerts/reservations`, payload, { headers });
    check(res, { 'status is 200': (r) => r.status === 200 });

    return JSON.parse(res.body).reservationId;
}

function processPayment(token, reservationId, currentId) {
    let headers = {
      'Content-Type': 'application/json',
      'Queue-Token': token,
    };
    let payload = JSON.stringify({
        userId: currentId,
        reservationId: reservationId,
    });
    let res = http.post(`${CONFIG.baseUrl}/api/payments`, payload, { headers });
    check(res, { 'status is 200': (r) => r.status === 200 });
}
