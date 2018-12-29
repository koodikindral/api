package com.estsoft.api.handler;

import com.estsoft.api.dto.AuthRequest;
import com.estsoft.api.dto.AuthResponse;
import com.estsoft.api.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class LoginHandler {

    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(AuthRequest.class).flatMap(req ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(userService.login(req), AuthResponse.class));
    }
}
