package com.example.authenticationserver.service;

import com.example.authenticationserver.JWT.JwtTokenProvider;
import com.example.authenticationserver.dto.IDPWDto;
import com.example.authenticationserver.dto.JwtToken;
import com.example.authenticationserver.entity.User;
import com.example.authenticationserver.enumerated.Authority;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.global.BaseResponse;
import com.example.authenticationserver.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.example.authenticationserver.global.BaseResponseStatus.LOGIN_EXPIRED;
import static com.example.authenticationserver.global.BaseResponseStatus.PASSWORD_NOT_MATCH;

@Service@Slf4j@RequiredArgsConstructor
public class UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public void test() {
        User user = User.builder()
                .username("test")
                .birthday(LocalDate.now())
                .realName("김실험")
                .email("aa@aa")
                .password(passwordEncoder.encode("test"))
                .joinDate(LocalDateTime.now())
                .gender(true)
                .authority(Authority.ROLE_PATIENT)
                .isEnabled(true)
                .isAccountNonLocked(true)
                .build();
        userRepository.save(user);
    }

    public User findByUsernameAndIsEnabledTrue(String username){
        return userRepository.findByUsernameAndIsEnabledTrue(username).orElseThrow();
    }



    TokenService tokenService;
    public void logout(HttpServletRequest request, HttpServletResponse response) throws BaseException {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN") && cookie.getPath().equals("/"))
                .findFirst()
                .get()
                .getValue();
        if(tokenService.existsByToken(refreshToken)) {
            tokenService.deleteByToken(refreshToken);
        }

        response.setHeader("Authorization","");

        response.addCookie(new Cookie("REFRESH_TOKEN","") {{
            setPath("/");
        }});
    }
}
