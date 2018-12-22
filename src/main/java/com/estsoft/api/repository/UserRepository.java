package com.estsoft.api.repository;

import com.estsoft.api.dto.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
}