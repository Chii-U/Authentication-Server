package com.example.authenticationserver.JWT;

import com.example.authenticationserver.entity.User;
import com.example.authenticationserver.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${paint.domain.name}")
    private String domain;
    private final Key key;

    @Autowired
    UserRepository userRepository;

    public JwtTokenProvider(@Value("${PAINT_JWT_KEY}") String secretKey) {
        byte[] secret = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(secret);
    }

    public String generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); // ,로

        //활용 가능한 정보중에 해당 정보가 있는지 (이때 비밀번호는 필터에서 인증되므로 유저네임이랑, 사용할수있는지만 대조하면 된다.)
        User user = userRepository.findByUsernameAndIsEnabledTrue(authentication.getName()).orElseThrow(IllegalArgumentException::new); //여기에 autentication의 이름과 리포의 이름을 대조하는 코드 필요, 맞으면 해당 객체를 불러옴
        //실제 액세스토큰 생성, 일단 이메일 or username, 클레임, 역할정도 넣으면 될 듯
        return doGenerateAccessToken(user.getUsername(), Map.of("username",user.getUsername(), "https://chi-iu.com/claims/what-role",authorities));
    }

    private String doGenerateAccessToken(String username, Map<String, Object> claims) {
        long now = (new Date()).getTime();
        Date acessTokenExpiresIn = new Date(now + 240000); // 4분정도... 60초 * 4분 * 1000ms
        return Jwts.builder()
                .setHeaderParam("type","jwt") // 많이들 씀, jwt라는거 알려는 줘야..ㅎ?
                .setIssuer(domain)
                .setIssuedAt(new Date())
                .setClaims(claims)
                .setSubject(username)
                .setExpiration(acessTokenExpiresIn) //이때 만료
                .signWith(key, SignatureAlgorithm.HS256) // 그냥 많이들 사용함
                .compact();
    }


    public boolean validateAccessToken(String token) {
    }

    public Authentication getAuthentication(String token) {
    }

    public boolean validateRefreshToken(String refreshToken, String token) {
    }

    public String reGenerateAccessToken(String token) {
    }

    public String reGenerateRefreshToken(String token) {
    }

    public boolean deleteRefreshToken(String refreshToken) {
    }
}
