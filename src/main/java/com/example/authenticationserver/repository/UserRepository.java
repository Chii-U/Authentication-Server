package com.example.authenticationserver.repository;


import com.example.authenticationserver.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findByUsernameAndIsEnabledTrue(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("{'username' : ?0}")
    @Update("{'$set': {'is_enabled': false}}")
    void disableByUsername(String username);

    @Query("{'username' : ?0}")
    @Update("{'$set': {'is_account_nonlocked': false}}")
    void lockByUsername(String username);

    @Query("{'email' : ?0}")
    @Update("{'$set': {'is_enabled': ?1}}")
    void updateEnable(String email, boolean enable);

    @Query("{'username' :  ?0}")
    @Update("{'$set': {'drop_date': ?1}}")
    void updateDrop(String username, LocalDateTime dropDate);

    @Query("db.user.find({\n" +
            "    drop_date: { $lte: new Date(new Date() - 90 * 24 * 60 * 60 * 1000) }\n" +
            "})")
    List<User> findAllByOldDrop();
}
