package com.example.authenticationserver.JWT;

import com.example.authenticationserver.entity.User;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${paint.domain.name}")
    private String domain;
    private final Key key;

    public JwtTokenProvider(@Value("${PAINT_JWT_KEY}") String secretKey) {
        byte[] secret = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(secret);
    }

    public String generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); // ,로

        User user; //여기에 autentication의 이름과 리포의 이름을 대조하는 코드 필요, 맞으면 해당 객체를 불러옴
        //실제 액세스토큰 생성, 일단 이메일 or username, 클레임, 역할정도 넣으면 될 듯
        return doGenerateAccessToken(user.getUsername(), Map.of("username",user.getUsername(), "https://chi-iu.com/claims/what-role",authorities));
    }

    private String doGenerateAccessToken(String username, Map<String, String> username1) {
    }

}
