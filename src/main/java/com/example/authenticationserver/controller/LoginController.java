package com.example.authenticationserver.controller;

import com.example.authenticationserver.dto.IDPWDto;
import com.example.authenticationserver.dto.JwtToken;
import com.example.authenticationserver.dto.SignUpDTO;
import com.example.authenticationserver.entity.User;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.global.BaseResponse;
import com.example.authenticationserver.service.LoginService;
import com.example.authenticationserver.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.authenticationserver.global.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users") // 인그레스에서 기본경로에 user가 있어서 매핑이 제대로 안되기에, 경로 이름을 바꿈
public class LoginController {
    @Autowired
    UserService userService;

    @Autowired
    LoginService loginService;


    @PostMapping("/login")
    public BaseResponse<JwtToken> login(
            @RequestBody IDPWDto idpwDto, HttpServletResponse response
    ) throws BaseException {
        JwtToken token = loginService.login(idpwDto,response);
        return new BaseResponse<>(token);
    }

    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) throws BaseException {
        userService.logout(request,response);
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping("/signup")
    public BaseResponse<Void> signup(
            @RequestBody SignUpDTO signUpDTO
    ) throws BaseException {
        userService.signup(signUpDTO);
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping("/dropout")
    public BaseResponse<Void> dropout(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String,String> data
    ) throws BaseException {
        if(!loginService.matchPassword(data.get("password"), user.getPassword())){
            throw new BaseException(PASSWORD_NOT_MATCH);
        }
        userService.dropout(user.getUsername());
        return new BaseResponse<Void>(SUCCESS);
    }

    // 취약해 보인다.
    @PostMapping("id-dup")
    public BaseResponse<Void> idDupCheck(
            @RequestBody Map<String,String> data
    ){
        //all 유저
        if(userService.existsByUsername(data.get("username"))){
            return new BaseResponse<Void>(DUPLICATION);
        }
        return new BaseResponse<Void>(SUCCESS);
    }
    @PostMapping("email-dup")
    public BaseResponse<Void> emailDupCheck(
            @RequestBody Map<String,String> data
    ){
        //all 유저
        if(userService.existsByEmail(data.get("email"))){
            return new BaseResponse<Void>(DUPLICATION);
        }
        return new BaseResponse<Void>(SUCCESS);
    }

    @GetMapping("/test")
    public BaseResponse<Void> test(){
        userService.test();
        return new BaseResponse<Void>(SUCCESS);
    }
}
