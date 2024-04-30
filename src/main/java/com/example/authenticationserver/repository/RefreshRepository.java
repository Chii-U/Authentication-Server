package com.example.authenticationserver.repository;

import com.example.authenticationserver.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository //이거 레디스 접근하는 리포임.
public interface RefreshRepository extends CrudRepository<RefreshToken,String> {
    Optional<RefreshToken> findByToken(String token);
    boolean deleteByToken(String token);
    boolean existsByToken(String token);

    RefreshToken save(RefreshToken refreshToken);
}
