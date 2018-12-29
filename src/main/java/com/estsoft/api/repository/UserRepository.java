package com.estsoft.api.repository;

import com.estsoft.api.dto.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findOneByEmail(final String email);
}