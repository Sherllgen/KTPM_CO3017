package com.example.subject.config;

import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FeignClientConfig {

    public ErrorDecoder errorDecoder() {
        return new FeignClientErrorDecoder();
    }

    private static class FeignClientErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, feign.Response response) {
            log.error("Feign client error: {} - Status: {}", methodKey, response.status());
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
