package com.estsoft.api.service;

import com.estsoft.api.config.Config;
import com.estsoft.api.dto.User;
import com.estsoft.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Flux<User> all() {
        return this.userRepository.findAll().subscribeOn(Config.APPLICATION_SCHEDULER);
    }

    public Mono<User> get(String id) {
        return this.userRepository.findById(id);
    }

    public Mono<User> create(String email) {
        return this.userRepository
                .save(new User(null, email, null));
    }

    public Mono<User> update(String id, String email) {
        return this.userRepository
                .findById(id)
                .map(p -> new User(p.getId(), email, null))
                .flatMap(this.userRepository::save);
    }

    public Mono<User> delete(String id) {
        return this.userRepository
                .findById(id)
                .flatMap(p -> this.userRepository.deleteById(p.getId()).thenReturn(p));
    }
}
