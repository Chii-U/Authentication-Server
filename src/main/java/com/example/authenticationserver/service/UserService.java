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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.example.authenticationserver.global.BaseResponseStatus.*;

@Service@Slf4j@RequiredArgsConstructor
public class UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

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

    public void signup(SignUpDTO signUpDTO,boolean isSocial) throws BaseException {
        if(userRepository.existsByUsername(signUpDTO.username()) || userRepository.existsByEmail(signUpDTO.email())) {
            throw new BaseException(EXISTS_USERNAME);
        }
        User user = userRepository.save(User.builder()
                .username(signUpDTO.username())
                .password(passwordEncoder.encode(signUpDTO.password()))
                .realName(signUpDTO.realName())
                .gender(signUpDTO.gender())
                .email(signUpDTO.email())
                .birthday(signUpDTO.birthday())
                .agreeMarketing(signUpDTO.agreeMarketing())
                .isEnabled(isSocial)
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
        userRepository.updateDrop(username,LocalDateTime.now());

    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void setEnable(String email, boolean enable) throws BaseException {
        User user = findByEmail(email);
        userRepository.updateEnable(email,enable);
    }

    public User findByEmail(String email) throws BaseException {
        return userRepository.findByEmail(email).orElseThrow(()-> new BaseException(USER_NOT_EXISTS));
    }

    public void hardDeleteDate() {
        List<User> users =  userRepository.findAllByOldDrop();
        for(User user : users) {
            String username = user.getUsername();
            Query query = new Query(Criteria.where("username").is(username));
            //user에 관련되어있는 테이블 삭제하기
            mongoTemplate.remove(query, "pain");
            mongoTemplate.remove(query, "predicted");
            //마지막으로 user 삭제
            userRepository.delete(user);
        }
    }
}
