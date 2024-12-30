package com.example.rsocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;

import java.net.URI;


class MessageTest {

    private RSocketRequester requester;

    private RSocketRequester requester2;
    
    @BeforeEach
    void setUp() {
        requester = RSocketRequester.builder()
                .rsocketStrategies(strategies -> strategies
                        .encoder(new Jackson2JsonEncoder())
                        .decoder(new Jackson2JsonDecoder()))
                .websocket(URI.create("ws://localhost:7050/rsocket"));

        requester2 = RSocketRequester.builder()
                .rsocketStrategies(strategies -> strategies
                        .encoder(new Jackson2JsonEncoder())
                        .decoder(new Jackson2JsonDecoder()))
                .websocket(URI.create("ws://localhost:7050/rsocket"));
    }
    
    @Test
    void messageTest() throws InterruptedException {

        // 메시지 구독
        requester.route("stream")
                .retrieveFlux(Message.class)
                .subscribe(msg -> {
                    System.out.println("사용자1 메시지 전달 받음" + msg);
                });


        // 메시지 전송
        Message message1 = new Message("qrqwt", "유저1: 안녕하세요!");

        requester.route("message")
                .data(message1)
                .send()
                .block();

        Thread.sleep(100); // 메시지 수신 대기

        requester2.route("stream")
                .retrieveFlux(Message.class)
                .subscribe(msg -> {
                    System.out.println("사용자2 메시지 전달 받음" + msg);
                });

        Message message2 = new Message("uasdf2", "유저2: 반갑습니다!");

        Thread.sleep(100); // 메시지 수신 대기

        requester2.route("message")
            .data(message2)
            .send()
            .block();
            
        Thread.sleep(100); // 메시지 수신 대기

        requester.route("message")
                .data(new Message("qrqwt", "유저1: 접속 후 메시지 전송 테스트"))
                .send()
                .block();

        Thread.sleep(100); // 메시지 수신 대기
    }
}