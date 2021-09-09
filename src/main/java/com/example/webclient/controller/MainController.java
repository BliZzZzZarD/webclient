package com.example.webclient.controller;

import com.example.webclient.exception.BusinessException;
import com.example.webclient.service.MainService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@RestController
@AllArgsConstructor
public class MainController {
    private static final String DEFAULT_ERROR_MESSAGE = "Request has finished with error";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(NON_NULL)
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(new JavaTimeModule());

    @NonNull
    private final MainService mainService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleDefaultException() {
        return new ResponseEntity<>(DEFAULT_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/get-player")
    public Mono<ResponseEntity<String>> getPlayer() {
        return mainService.getPlayer()
                //.map(this::serializeResponseObject)
                .map(ResponseEntity::ok)
                .onErrorResume(this::handleErrorReturn);
    }

    @SneakyThrows
    private <T> String serializeResponseObject(T responseObject) {
        return objectMapper.writeValueAsString(responseObject);
    }

    private Mono<ResponseEntity<String>> handleErrorReturn(Throwable throwable) {
        if (throwable instanceof BusinessException) {
            return Mono.just(ResponseEntity.badRequest().body(throwable.getMessage()));
        } else {
            return Mono.just(new ResponseEntity<>(DEFAULT_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
