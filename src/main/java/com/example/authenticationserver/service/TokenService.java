package com.example.authenticationserver.service;

import com.example.authenticationserver.entity.RefreshToken;
import com.example.authenticationserver.repository.RefreshRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service@Slf4j
public class TokenService {
    @Autowired
    RefreshRepository refreshRepository;

    public RefreshToken findByToken(String refreshToken) {
        return refreshRepository.findByToken(refreshToken).orElseThrow();
    }

    public boolean deleteByToken(String refreshtoken) {
        return refreshRepository.deleteByToken(refreshtoken);
    }

    public boolean save(RefreshToken refreshToken) {
        return refreshRepository.save(refreshToken).equals(refreshToken);
    }
}
