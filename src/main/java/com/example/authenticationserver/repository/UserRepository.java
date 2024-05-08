package com.example.authenticationserver.repository;


import com.example.authenticationserver.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findByUsernameAndIsEnabledTrue(String username);

    boolean existsByEmail(String email);
}
