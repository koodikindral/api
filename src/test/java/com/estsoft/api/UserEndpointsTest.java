package com.estsoft.api;

import com.estsoft.api.dto.User;
import com.estsoft.api.handler.UserHandler;
import com.estsoft.api.repository.UserRepository;
import com.estsoft.api.router.UserRouter;
import com.estsoft.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UserRouter.class, UserService.class, UserHandler.class})
@WebFluxTest
public class UserEndpointsTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private UserRepository repository;

    @Test
    public void getAll() {

        System.out.println("Tere");

        Mockito
                .when(this.repository.findAll())
                .thenReturn(Flux.just(new User("1", "A"), new User("2", "B")));


        this.client
                .get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo("1")
                .jsonPath("$.[0].email").isEqualTo("A")
                .jsonPath("$.[1].id").isEqualTo("2")
                .jsonPath("$.[1].email").isEqualTo("B");
    }

    @Test
    public void save() {
        User data = new User("123", UUID.randomUUID().toString() + "@email.com");
        Mockito
                .when(this.repository.save(Mockito.any(User.class)))
                .thenReturn(Mono.just(data));
        MediaType jsonUtf8 = MediaType.APPLICATION_JSON_UTF8;
        this
                .client
                .post()
                .uri("/users")
                .contentType(jsonUtf8)
                .body(Mono.just(data), User.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(jsonUtf8);
    }

    @Test
    public void delete() {
        User data = new User("123", UUID.randomUUID().toString() + "@email.com");
        Mockito
                .when(this.repository.findById(data.getId()))
                .thenReturn(Mono.just(data));
        Mockito
                .when(this.repository.deleteById(data.getId()))
                .thenReturn(Mono.empty());
        this
                .client
                .delete()
                .uri("/users/" + data.getId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void update() {
        User data = new User("123", UUID.randomUUID().toString() + "@email.com");

        Mockito
                .when(this.repository.findById(data.getId()))
                .thenReturn(Mono.just(data));

        Mockito
                .when(this.repository.save(data))
                .thenReturn(Mono.just(data));

        this
                .client
                .put()
                .uri("/users/" + data.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(data), User.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void getById() {

        User data = new User("1", "A");

        Mockito
                .when(this.repository.findById(data.getId()))
                .thenReturn(Mono.just(data));

        this.client
                .get()
                .uri("/users/" + data.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isEqualTo(data.getId())
                .jsonPath("$.email").isEqualTo(data.getEmail());
    }
}