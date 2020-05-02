package com.example.reactivewebsocketclient;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.synchronoss.cloud.nio.multipart.util.MimeUtility;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@SpringBootApplication
@Slf4j
public class ReactiveWebsocketClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveWebsocketClientApplication.class, args);
    }

    @Bean
    RSocket rSocket() {
        return RSocketFactory
                .connect()
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON_VALUE)
                .frameDecoder(PayloadDecoder.ZERO_COPY)
                .transport(TcpClientTransport.create(7100))
                .start()
                .block();

    }

    //    @Bean
//    RSocketRequester requester(RSocketStrategies rSocketStrategies) {
//        return RSocketRequester.wrap(this.rSocket(), MimeTypeUtils.APPLICATION_JSON, MimeTypeUtils.APPLICATION_JSON, rSocketStrategies);
//    }
    @Bean
    RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies) {
        return RSocketRequester.builder()
                .rsocketStrategies(rSocketStrategies)
                .connectTcp("localhost", 7100)
                .block();
    }
}

@RestController
class webSocket {

    @Autowired
    RSocketRequester requester;

//    @GetMapping(value = "sse/data", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
//    public Publisher<String> socket() {
//
//        return requester.route("echo")
//                .data("hello")
//                .retrieveFlux(String.class);
//
//    }

    @GetMapping(value = "sse/data", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Publisher<User> socket() {

        return requester.route("echo")
                .data("hello")
                .retrieveFlux(User.class);

    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class User {

    @Id
    private String id;
    private String fName;
    private String lName;
    private int age;

}


