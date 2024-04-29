package com.example.authenticationserver.config;

import com.example.authenticationserver.JWT.JwtTokenProvider;
import com.example.authenticationserver.JWT.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        System.out.println("=====인증 서버 정상 실행중, 서버 버전 0.0.1 =====");

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 안씀 토큰 방식으로
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET).permitAll() // 일단 Get메소드는 인증에서 무조건 패스하게
                        .requestMatchers(HttpMethod.POST).hasRole("PATIENT") // 이 서비스의 기본 권한은 환자.
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll() // 프로바이더에서 나온건 허용
                        .anyRequest().authenticated() // 인증 필요
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) // 이 필터 체인 이전에 사용할 필터 설정
                .build();

    }

    // 패스워드는 들어온다면 인코딩된 상태로 db에 저장되어야 하므로.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt
        BCryptPasswordEncoder pwEncoder = new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
