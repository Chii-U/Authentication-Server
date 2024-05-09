package com.example.authenticationserver.repository;


import com.example.authenticationserver.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findByUsernameAndIsEnabledTrue(String username);

    boolean existsByEmail(String email);

    @Query("{'username' : ?0}")
    @Update("{'$set': {'is_enabled': false}}")
    void disableByUsername(String username);

    @Query("{'username' : ?0}")
    @Update("{'$set': {'is_account_nonlocked': false}}")
    void lockByUsername(String username);

}
