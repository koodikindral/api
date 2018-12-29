package com.estsoft.api.service;

import com.estsoft.api.auth.PBKDF2Encoder;
import com.estsoft.api.config.Config;
import com.estsoft.api.dto.AuthRequest;
import com.estsoft.api.dto.AuthResponse;
import com.estsoft.api.dto.User;
import com.estsoft.api.repository.UserRepository;
import com.estsoft.api.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PBKDF2Encoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public UserService(UserRepository userRepository, PBKDF2Encoder passwordEncoder, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Flux<User> all() {
        return this.userRepository.findAll().subscribeOn(Config.APPLICATION_SCHEDULER);
    }

    public Mono<User> get(String id) {
        return this.userRepository.findById(id);
    }

    public Mono<User> create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.userRepository
                .save(user);
    }

    public Mono<User> update(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.userRepository
                .findById(user.getId())
                .flatMap(this.userRepository::save);
    }

    public Mono<User> delete(String id) {
        return this.userRepository
                .findById(id)
                .flatMap(p -> this.userRepository.deleteById(p.getId()).thenReturn(p));
    }

    public Mono<AuthResponse> login(AuthRequest authRequest) {
        return this.userRepository.findOneByEmail(authRequest.getEmail())
                .filter(user -> passwordEncoder.matches(authRequest.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid password")))
                .flatMap(user -> Mono.just(new AuthResponse(jwtUtil.generateToken(user))))
                .doOnError(throwable -> log.error("Error on creating JWT token", throwable));
    }
}
