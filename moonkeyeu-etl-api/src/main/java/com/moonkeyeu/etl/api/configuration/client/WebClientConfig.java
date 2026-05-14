package com.moonkeyeu.etl.api.configuration.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;


/**
 * The WebClient is configured with timeouts and memory limits for handling HTTP requests and responses.
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates and configures a WebClient bean.
     * The WebClient is built using a Reactor Netty HttpClient with the following settings:
     * - Read timeout of 30 seconds.
     * - Write timeout of 30 seconds.
     * - Connect timeout of 30 seconds.
     * - Response timeout of 60 seconds.
     * - Maximum in-memory buffer size of 20 MB.
     *
     * @return a configured WebClient instance.
     */

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(120))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(60))
                        .addHandlerLast(new WriteTimeoutHandler(60))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024))
                .build();
    }

}
