package com.example.authenticationserver.JWT;

import com.example.authenticationserver.entity.User;
import com.example.authenticationserver.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.security.SecurityException;
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

    // 생성자~ 일단 가장중요한 키 초기화
    public JwtTokenProvider(@Value("${PAINT_JWT_KEY}") String secretKey) {
        byte[] secret = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(secret);
    }

    //login할때
    public String generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); // ,로

        //활용 가능한 정보중에 해당 정보가 있는지 (이때 비밀번호는 필터에서 인증되므로 유저네임이랑, 사용할수있는지만 대조하면 된다.)
        User user = userRepository.findByUsernameAndIsEnabledTrue(authentication.getName()).orElseThrow(IllegalArgumentException::new); //여기에 autentication의 이름과 리포의 이름을 대조하는 코드 필요, 맞으면 해당 객체를 불러옴
        //실제 액세스토큰 생성, 일단 이메일 or username, 클레임, 역할정도 넣으면 될 듯
        return doGenerateAccessToken(user.getUsername(), Map.of("username",user.getUsername(), "https://chi-iu.com/claims/what-role",authorities));
    }

    //
    private String doGenerateAccessToken(String username, Map<String, Object> claims) {
        long now = (new Date()).getTime();
        Date acessTokenExpiresIn = new Date(now + 240000); // 4분정도... 60초 * 4분 * 1000ms
        // jwt 토큰을 만드는 것!
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

    // 들어온 액세스토큰의 시간 값을 포함한 등등이 유효한지
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e ) {
            System.out.println("JWT는 아닌듯");
        } catch (UnsupportedJwtException e) {
            System.out.println("내 사이트에서 만들어진 건 아닌듯");
        } catch (IllegalArgumentException e) {
            System.out.println("클레임 뭔데?");
        }
        return false;
    }

    // 들어온 액세스토큰에서 인증정보 빼기
    public Authentication getAuthentication(String token) {
    }

    // 들어온 리프레시토큰이 액세스토큰과 비교했을때 같은 사람껀지, 유효한지.
    public boolean validateRefreshToken(String refreshToken, String token) {
    }

    // 액세스토큰을 가지고 인증정보를 빼서 유효기간을 늘리기
    public String reGenerateAccessToken(String token) {
    }

    // rtr 방식을 위해 들어온 리프레시토큰을 대체하기, 특히 Ttl값은 이전 토큰을 파싱하여 가져다 쓴다.
    public String reGenerateRefreshToken(String token) {
    }

    // 로그아웃 시 현재 요청이 들어온 리프레시 토큰을 리포에서 삭제한다.
    public boolean deleteRefreshToken(String refreshToken) {
    }
}
