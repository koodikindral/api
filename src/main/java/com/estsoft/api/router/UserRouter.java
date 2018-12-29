package com.estsoft.api.router;

import com.estsoft.api.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouter {
    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return route(RequestPredicates.GET("/users"), handler::all)
                .andRoute(RequestPredicates.GET("/users/{id}"), handler::getById)
                .andRoute(RequestPredicates.DELETE("/users/{id}"), handler::deleteById)
                .andRoute(RequestPredicates.POST("/users"), handler::create)
                .andRoute(RequestPredicates.PUT("/users/{id}"), handler::updateById);
    }
}
