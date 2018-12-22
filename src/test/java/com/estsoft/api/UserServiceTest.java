package com.estsoft.api;

import com.estsoft.api.dto.User;
import com.estsoft.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;


@DataMongoTest
@Import(UserService.class)
public class UserServiceTest {

    private final UserService service;

    public UserServiceTest(@Autowired UserService service) {
        this.service = service;
    }


    @Test
    public void save() {
        Mono<User> profileMono = this.service.create("email@email.com");
        StepVerifier
                .create(profileMono)
                .expectNextMatches(saved -> StringUtils.hasText(saved.getId()))
                .verifyComplete();
    }

    @Test
    public void delete() {
        String test = "test";
        Mono<User> deleted = this.service
                .create(test)
                .flatMap(saved -> this.service.delete(saved.getId()));
        StepVerifier
                .create(deleted)
                .expectNextMatches(profile -> profile.getEmail().equalsIgnoreCase(test))
                .verifyComplete();
    }

    @Test
    public void update() throws Exception {
        Mono<User> saved = this.service
                .create("test")
                .flatMap(p -> this.service.update(p.getId(), "test1"));
        StepVerifier
                .create(saved)
                .expectNextMatches(p -> p.getEmail().equalsIgnoreCase("test1"))
                .verifyComplete();
    }

    @Test
    public void getById() {
        String test = UUID.randomUUID().toString();
        Mono<User> deleted = this.service
                .create(test)
                .flatMap(saved -> this.service.get(saved.getId()));
        StepVerifier
                .create(deleted)
                .expectNextMatches(profile -> StringUtils.hasText(profile.getId()) && test.equalsIgnoreCase(profile.getEmail()))
                .verifyComplete();
    }
}
