package com.example.rsocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Controller
public class MessageController {
    private final Sinks.Many<Message> messageSink;

    @ConnectMapping
    public void connect() {
        System.out.println("접속!!!");
    }
    
    public MessageController() {
        this.messageSink = Sinks.many().multicast().directAllOrNothing();
    }
    
    @MessageMapping("message")
    public Mono<Void> sendMessage(Message message) {
        System.out.println("메시지 전송 시작");
        messageSink.tryEmitNext(message);
        return Mono.empty();
    }
    
    @MessageMapping("stream")
    public Flux<Message> receiveMessages() {
        System.out.println("구독 시작");
        return messageSink.asFlux();
    }
}