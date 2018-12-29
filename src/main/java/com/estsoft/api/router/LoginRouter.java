package com.estsoft.api.router;

import com.estsoft.api.handler.LoginHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class LoginRouter {

    @Bean
    public RouterFunction<ServerResponse> loginRoutes(LoginHandler handler) {
        return route(RequestPredicates.POST("/login"), handler::login);
    }
}
