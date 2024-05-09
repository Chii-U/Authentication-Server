package com.example.authenticationserver.service;

import com.example.authenticationserver.dto.SignUpDTO;
import com.example.authenticationserver.entity.User;
import com.example.authenticationserver.enumerated.Authority;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static com.example.authenticationserver.global.BaseResponseStatus.*;

@Service@Slf4j@RequiredArgsConstructor
public class UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    MongoTemplate mongoTemplate;

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

    public void signup(SignUpDTO signUpDTO) throws BaseException {
        if(userRepository.existsById(signUpDTO.username()) || userRepository.existsByEmail(signUpDTO.email())) {
            throw new BaseException(EXISTS_USERNAME);
        }
        User user = userRepository.save(User.builder()
                .username(signUpDTO.username())
                .password(signUpDTO.password())
                .realName(signUpDTO.realName())
                .gender(signUpDTO.gender())
                .email(signUpDTO.email())
                .birthday(signUpDTO.birthday())
                .agreeMarketing(signUpDTO.agreeMarketing())
                .isEnabled(false)
                .authority(Authority.ROLE_PATIENT)
                .isAccountNonLocked(true)
                .joinDate(LocalDateTime.now())
                .build()
        );
    }

    public void dropout(String username) {
        //soft
        userRepository.disableByUsername(username);
        userRepository.lockByUsername(username);

    }

}
