package com.estsoft.api;

import com.estsoft.api.auth.PBKDF2Encoder;
import com.estsoft.api.dto.AuthRequest;
import com.estsoft.api.dto.AuthResponse;
import com.estsoft.api.dto.Role;
import com.estsoft.api.dto.User;
import com.estsoft.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


@DataMongoTest
@ComponentScan("com.estsoft.api")
@Import({UserService.class, PBKDF2Encoder.class})
public class UserServiceTest {

    @Autowired
    private UserService service;

    @Autowired
    private PBKDF2Encoder pbkdf2Encoder;

    private User user = new User("123", UUID.randomUUID().toString() + "@email.com", "tere", new ArrayList<>(Arrays.asList(Role.ROLE_USER)));

    @Test
    public void save() {
        Mono<User> profileMono = this.service.create(user);
        StepVerifier
                .create(profileMono)
                .expectNextMatches(saved -> StringUtils.hasText(saved.getId()))
                .verifyComplete();
    }

    @Test
    public void delete() {
        Mono<User> deleted = this.service
                .create(user)
                .flatMap(saved -> this.service.delete(saved.getId()));
        StepVerifier
                .create(deleted)
                .expectNextMatches(profile -> profile.getEmail().equalsIgnoreCase(user.getEmail()))
                .verifyComplete();
    }

    @Test
    public void update() throws Exception {
        Mono<User> saved = this.service
                .create(user)
                .flatMap(p -> this.service.update(user));
        StepVerifier
                .create(saved)
                .expectNextMatches(p -> p.getEmail().equalsIgnoreCase(user.getEmail()))
                .verifyComplete();
    }

    @Test
    public void getById() {
        Mono<User> deleted = this.service
                .create(user)
                .flatMap(saved -> this.service.get(saved.getId()));
        StepVerifier
                .create(deleted)
                .expectNextMatches(profile -> StringUtils.hasText(profile.getId()) && user.getEmail().equalsIgnoreCase(profile.getEmail()))
                .verifyComplete();
    }

    @Test
    public void login() {
        final AuthRequest authRequest = new AuthRequest(user.getEmail(), user.getPassword());
        Mono<AuthResponse> login = this.service
                .create(user)
                .flatMap(l -> this.service.login(authRequest));
        StepVerifier
                .create(login)
                .expectNextMatches(l -> l.getToken().length() > 0)
                .verifyComplete();

    }
}
