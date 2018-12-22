package com.estsoft.api.handler;

import com.estsoft.api.dto.User;
import com.estsoft.api.service.UserService;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class UserHandler {

    private UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> getById(ServerRequest r) {
        return defaultReadResponse(this.userService.get(id(r)));
    }

    public Mono<ServerResponse> all(ServerRequest r) {
        return defaultReadResponse(this.userService.all());
    }

    public Mono<ServerResponse> updateById(ServerRequest r) {
        Flux<User> id = r.bodyToFlux(User.class)
                .flatMap(p -> this.userService.update(id(r), p.getEmail()));
        return defaultReadResponse(id);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Flux<User> flux = request
                .bodyToFlux(User.class)
                .flatMap(toWrite -> this.userService.create(toWrite.getEmail()));
        return defaultWriteResponse(flux);
    }

    public Mono<ServerResponse> deleteById(ServerRequest r) {
        return defaultReadResponse(this.userService.delete(id(r)));
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<User> user) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(user, User.class);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<User> user) {
        return Mono
                .from(user)
                .flatMap(p -> ServerResponse
                        .created(URI.create("/profiles/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }


    private static String id(ServerRequest r) {
        return r.pathVariable("id");
    }
}
