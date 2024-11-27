import http from 'k6/http';
import {check, sleep} from 'k6';
import exec from 'k6/execution';

const CONFIG = {
    baseUrl: 'http://spring-boot-app:8080',
    concertId: 1,
    scheduleId: 1,
    userId: 1,
};

export let options = {
    vus: 100, // 동시 가상 유저 수
    duration: '1m', // 테스트 시간
    thresholds: { http_req_duration: ['p(95)<300'] }, // 95% 요청이 300ms 이하
};

export default function () {
    let token = getWaitingToken();

    let headers = {
        'Content-Type': 'application/json',
        'Queue-Token': token,
    };
    
    getQueueStatus(headers);
    getConcertSchedules(headers);
    getConcertSeats(headers);
    let reservationId = reserveSeat(headers);
    processPayment(headers, reservationId);

    sleep(1);
}

export function teardown(data) {
    console.log('테스트 완료');
}

function getWaitingToken() {
    let headers = {
        'Content-Type': 'application/json',
    };
    let payload = JSON.stringify({
        userId: CONFIG.userId,
        concertId: CONFIG.concertId,
        concertScheduleId: CONFIG.scheduleId,
    });
    let res = http.post(`${CONFIG.baseUrl}/api/waiting-queues`, payload, { headers });
    check(res, { 'status is 200': (r) => r.status === 200 });

    return JSON.parse(res.body).token;
}

function getQueueStatus(headers) {
    let res = http.get(`${CONFIG.baseUrl}/api/waiting-queues`, { headers }, );
    check(res, { 'status is 200': (r) => r.status === 200 });
}

function getConcertSchedules(headers) {
    let res = http.get(`${CONFIG.baseUrl}/api/concerts/${CONFIG.concertId}`, { headers });
    check(res, { 'status is 200': (r) => r.status === 200 });
}

function getConcertSeats(headers) {
    let res = http.get(
        `${CONFIG.baseUrl}/api/concerts/${CONFIG.concertId}/schedules/${CONFIG.scheduleId}/seats`,
        { headers }
    );
    check(res, { 'status is 200': (r) => r.status === 200 });
}

function reserveSeat(headers) {
    let payload = JSON.stringify({
        userId: 1,
        concertId: 1,
        scheduleId: 1,
        seatId: exec.scenario.iterationInTest + 292855,
    });
    let res = http.post(`${CONFIG.baseUrl}/api/concerts/reservations`, payload, { headers });
    check(res, { 'status is 200': (r) => r.status === 200 });

    return JSON.parse(res.body).reservationId;
}

function processPayment(headers, reservationId) {
    let payload = JSON.stringify({
        userId: 1,
        reservationId: reservationId,
    });
    let res = http.post(`${CONFIG.baseUrl}/api/payments`, payload, { headers });
    check(res, { 'status is 200': (r) => r.status === 200 });
}
