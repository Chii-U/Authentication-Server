package com.example.authenticationserver.service;

import com.example.authenticationserver.JWT.JwtTokenProvider;
import com.example.authenticationserver.dto.IDPWDto;
import com.example.authenticationserver.dto.JwtToken;
import com.example.authenticationserver.entity.Account;
import com.example.authenticationserver.entity.User;
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

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static com.example.authenticationserver.global.BaseResponseStatus.PASSWORD_NOT_MATCH;

@Service@Slf4j@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    UserRepository userRepository;

    @Override @Transactional
    public UserDetails loadUserByUsername(String username) {
        User user = findByUsernameAndIsEnabledTrue(username);
        return new Account(username, user.getPassword(), List.of(new SimpleGrantedAuthority(user.getAuthority().getAuthority())));
    }
    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;




    @Transactional
    public JwtToken login(IDPWDto idpwDto, HttpServletResponse response) throws BaseException {
        matchPassword(idpwDto.username(),idpwDto.password());

        UserDetails user = loadUserByUsername(idpwDto.username());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword(),user.getAuthorities());
        try{
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            String accessToken = jwtTokenProvider.generateAccessToken(authentication);

            response.addCookie(new Cookie("REFRESH_TOKEN",jwtTokenProvider.generateRefreshToken(accessToken,false)){{ setMaxAge(3600); setPath("/"); }});

            return JwtToken.builder()
                    .accessToken(accessToken)
                    .grantType("Bearer")
                    .build();
        } catch (AuthenticationException | NoSuchElementException e) {
            throw new BaseException(PASSWORD_NOT_MATCH);
        }
    }

    public boolean matchPassword(String username, String password) throws BaseException {
        UserDetails user = loadUserByUsername(username);
        if(!passwordEncoder.matches(password,user.getPassword())) {
            throw new BaseException(PASSWORD_NOT_MATCH);
        }else
            return true;
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
