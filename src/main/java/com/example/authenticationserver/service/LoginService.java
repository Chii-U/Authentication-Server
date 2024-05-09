package com.example.authenticationserver.service;

import com.example.authenticationserver.JWT.JwtTokenProvider;
import com.example.authenticationserver.dto.IDPWDto;
import com.example.authenticationserver.dto.JwtToken;
import com.example.authenticationserver.dto.SignUpDTO;
import com.example.authenticationserver.entity.User;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.global.BaseResponseStatus;
import jakarta.servlet.http.Cookie;
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

import java.util.List;
import java.util.NoSuchElementException;

import static com.example.authenticationserver.global.BaseResponseStatus.PASSWORD_NOT_MATCH;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private UserService userService;

    @Override @Transactional
    public UserDetails loadUserByUsername(String username) {
        User user = userService.findByUsernameAndIsEnabledTrue(username);
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), List.of(new SimpleGrantedAuthority(user.getAuthority().name())));
    }

    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtToken login(IDPWDto idpwDto, HttpServletResponse response) throws BaseException {
        UserDetails user = loadUserByUsername(idpwDto.username());
        if(!passwordEncoder.matches(idpwDto.password(),user.getPassword())) {
            throw new BaseException(PASSWORD_NOT_MATCH);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),idpwDto.password(),user.getAuthorities());
        try{
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);

            response.addCookie(new Cookie("REFRESH_TOKEN",jwtTokenProvider.generateRefreshToken(accessToken,false)){{ setMaxAge(3600); setPath("/"); }});

            return JwtToken.builder()
                    .accessToken(accessToken)
                    .grantType("Bearer")
                    .build();
        } catch (AuthenticationException | NoSuchElementException e) {
            System.out.println(e.getMessage());
            throw new BaseException(PASSWORD_NOT_MATCH);
        }
    }

    public boolean matchPassword(String password, String enPassword) {
        return passwordEncoder.matches(password,enPassword);
    }
}
