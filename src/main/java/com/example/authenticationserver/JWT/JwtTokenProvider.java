package com.example.authenticationserver.JWT;

import com.example.authenticationserver.entity.Account;
import com.example.authenticationserver.entity.RefreshToken;
import com.example.authenticationserver.entity.User;
import com.example.authenticationserver.service.TokenService;
import com.example.authenticationserver.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${paint.domain.name}")
    private String domain;
    private final Key accessKey;
    private final Key refreshKey;


    UserService userService;
    TokenService tokenService;

    // 생성자~ 일단 가장중요한 키 초기화
    public JwtTokenProvider(@Value("${PAINT_JWT_KEY}") String secretKey, @Value("${PAINT_REFRESH_SECRET}") String refreshKey) {
        byte[] secret = Decoders.BASE64.decode(secretKey);
        this.accessKey = Keys.hmacShaKeyFor(secret);
        byte[] secret2 = Decoders.BASE64.decode(refreshKey);
        this.refreshKey = Keys.hmacShaKeyFor(secret2);
    }

    //login할때
    public String generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); // ,로

        //활용 가능한 정보중에 해당 정보가 있는지 (이때 비밀번호는 필터에서 인증되므로 유저네임이랑, 사용할수있는지만 대조하면 된다.)
        User user = userService.findByUsernameAndIsEnabledTrue(authentication.getName()); //여기에 autentication의 이름과 리포의 이름을 대조하는 코드 필요, 맞으면 해당 객체를 불러옴
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
                .signWith(accessKey, SignatureAlgorithm.HS256) // 그냥 많이들 사용함
                .compact();
    }

    // 들어온 액세스토큰의 시간 값을 포함한 등등이 유효한지
    public boolean validateAccessToken(String token) {
        return validateAccessToken(token,accessKey);
    }
    // 리프레시 검증하는 경우때문에 오버로딩함.
    private boolean validateAccessToken(String token,Key key) {
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
        //클레임빼는 함수는 따로 구현해야함
        Claims claims = parseClaims(token);

        if(claims.get("https://chi-iu.com/claims/what-role") == null) {
            throw new RuntimeException("우리꺼 아니잖아요.");
        }

        Collection<GrantedAuthority> authorities = Arrays.stream(claims.get("https://chi-iu.com/claims/what-role").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        UserDetails principal = new Account((String) claims.get("username"),"",authorities);
        return new UsernamePasswordAuthenticationToken(principal, "",authorities);
    }

    //어떻게든 클레임을 제공함
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 들어온 리프레시토큰이 액세스토큰과 비교했을때 같은 사람껀지, 유효한지.
    public boolean validateRefreshToken(String refreshToken, String token) {
        try {
            RefreshToken tokenEntity = tokenService.findByToken(refreshToken);
            String username = getAuthentication(token).getName();
            User user = userService.findByUsernameAndIsEnabledTrue(username);

            if (user.getUsername().equals(tokenEntity.getUsername())) {
                System.out.println("ok 맞음, 레디스에 있던 아이디랑, 니가 찾는 db에 있는 유저 아이디 일치함");
                return validateAccessToken(tokenEntity.getToken(),refreshKey);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // 액세스토큰을 가지고 인증정보를 빼서 유효기간을 늘리기
    public String reGenerateAccessToken(String token) {
        Claims claims = parseClaims(token);
        return doGenerateAccessToken(claims.getSubject(),Map.of("username",claims.getSubject(), "https://chi-iu.com/claims/what-role",claims.get("https://chi-iu.com/claims/what-role")));
    }

    //귀찮으니 액세스토큰이랑 같을 때만들테니까 그냥 액세스토큰재활용식
    public String generateRefreshToken(String accessToken,boolean reGen) {
        long now = (new Date()).getTime();
        long term = 3600 * 10000; // 한 시간 정도... 60초 * 60분 * 1000ms
        Date refreshTokenExpiresIn = new Date(now + term);
        // jwt 토큰을 만드는 것!
        String newRT = Jwts.builder()
                .setHeaderParam("type","jwt") // 많이들 씀, jwt라는거 알려는 줘야..ㅎ?
                .setIssuer(domain)
                .setIssuedAt(new Date())
                .setClaims(parseClaims(accessToken))
                .setSubject(parseClaims(accessToken).getSubject())
                .setExpiration(refreshTokenExpiresIn) //이때 만료
                .signWith(refreshKey, SignatureAlgorithm.HS256) // 그냥 많이들 사용함
                .compact();

        //레디스에 저장 리제너레이트인경우에는 리젠 코드에서 실행...
        if(!reGen) {
            saveRefreshToken(term, newRT, parseClaims(accessToken).getSubject());
        }
        return newRT;

    }
    private boolean saveRefreshToken(long ttl, String token, String username){
        RefreshToken refreshToken = RefreshToken.builder()
                .ttl(ttl)
                .token(token)
                .username(username)
                .build();
        return tokenService.save(refreshToken);
    }


    // rtr 방식을 위해 들어온 리프레시토큰을 대체하기, 특히 Ttl값은 이전 토큰을 파싱하여 가져다 쓴다.
    public String reGenerateRefreshToken(String refreshToken, String accessToken) {
        try {
            RefreshToken tokenEntity = tokenService.findByToken(refreshToken);
            long ttl = tokenEntity.getTtl();
            String username = tokenEntity.getUsername();
            if(deleteRefreshToken(refreshToken))
                System.out.println("not deleted previous refresh token");

            String newRT = generateRefreshToken(accessToken, true);

            if(!saveRefreshToken(ttl, newRT, username))
                System.out.println("not saved new refresh token");

            return newRT;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // 로그아웃 시 현재 요청이 들어온 리프레시 토큰을 리포에서 삭제한다.
    public boolean deleteRefreshToken(String refreshToken) {
        return tokenService.deleteByToken(refreshToken);
    }
}
