package com.example.webclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Service
@RequiredArgsConstructor
public class MainService {
    private final WebClientService webClientService;

    private final static String PATH = "latest.js";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(NON_NULL)
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(new JavaTimeModule());

    public Mono<String> getPlayer() {
        return webClientService.getData(PATH);
    }

    @SneakyThrows
    private <T> T deserializeResponse(String s, Class<T> clazz) {
        return objectMapper.readValue(s, clazz);
    }
}
