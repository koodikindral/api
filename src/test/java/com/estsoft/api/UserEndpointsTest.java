package com.estsoft.api;

import com.estsoft.api.auth.PBKDF2Encoder;
import com.estsoft.api.dto.AuthRequest;
import com.estsoft.api.dto.Role;
import com.estsoft.api.dto.User;
import com.estsoft.api.repository.UserRepository;
import com.estsoft.api.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@AutoConfigureWebTestClient
@ComponentScan("com.estsoft.api")
public class UserEndpointsTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PBKDF2Encoder pbkdf2Encoder;

    @MockBean
    private UserRepository repository;

    private final User user0 = new User("123", UUID.randomUUID().toString() + "@email.com", "tere", new ArrayList<>(Arrays.asList(Role.ROLE_USER)));
    private final User user1 = new User("456", UUID.randomUUID().toString() + "@email2.com", "asd", new ArrayList<>(Arrays.asList(Role.ROLE_USER)));

    @BeforeEach
    public void setUp() {
        final String token = jwtUtil.generateToken(user0);

        client = client
                .mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    @Test
    public void getAll() {
        Mockito
                .when(this.repository.findAll())
                .thenReturn(Flux.just(user0, user1));

        this.client
                .get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(user0.getId())
                .jsonPath("$.[0].email").isEqualTo(user0.getEmail())
                .jsonPath("$.[1].id").isEqualTo(user1.getId())
                .jsonPath("$.[1].email").isEqualTo(user1.getEmail());
    }

    @Test
    public void save() {
        Mockito
                .when(this.repository.save(Mockito.any(User.class)))
                .thenReturn(Mono.just(user0));
        MediaType jsonUtf8 = MediaType.APPLICATION_JSON_UTF8;
        this
                .client
                .post()
                .uri("/users")
                .contentType(jsonUtf8)
                .body(Mono.just(user0), User.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(jsonUtf8);
    }

    @Test
    public void delete() {
        Mockito
                .when(this.repository.findById(user0.getId()))
                .thenReturn(Mono.just(user0));
        Mockito
                .when(this.repository.deleteById(user0.getId()))
                .thenReturn(Mono.empty());
        this
                .client
                .delete()
                .uri("/users/" + user0.getId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void update() {
        Mockito
                .when(this.repository.findById(user0.getId()))
                .thenReturn(Mono.just(user0));

        Mockito
                .when(this.repository.save(user0))
                .thenReturn(Mono.just(user0));

        this
                .client
                .put()
                .uri("/users/" + user0.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(user0), User.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void getById() {
        Mockito
                .when(this.repository.findById(user0.getId()))
                .thenReturn(Mono.just(user0));

        this.client
                .get()
                .uri("/users/" + user0.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isEqualTo(user0.getId())
                .jsonPath("$.email").isEqualTo(user0.getEmail());
    }

    @Test
    public void login() {
        final AuthRequest authRequest = new AuthRequest(user0.getEmail(), user0.getPassword());
        User user = user0;
        user.setPassword(pbkdf2Encoder.encode(user.getPassword()));

        Mockito
                .when(this.repository.findOneByEmail(user0.getEmail()))
                .thenReturn(Mono.just(user));
        this.client
                .post()
                .uri("/login")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(authRequest), AuthRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.token").exists();
    }
}