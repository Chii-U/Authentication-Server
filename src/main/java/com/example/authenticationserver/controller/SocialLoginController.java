package com.example.authenticationserver.controller;

import com.example.authenticationserver.dto.JwtToken;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.global.BaseResponse;
import com.example.authenticationserver.global.BaseResponseStatus;
import com.example.authenticationserver.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.example.authenticationserver.global.BaseResponseStatus.BAD_ACCESS;

@RestController
@RequestMapping("/api/v1/users/oauth2")
public class SocialLoginController {

    @Autowired
    private OAuthService oAuthService;

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    @GetMapping("/code/{registrationId}") // id = google , kakao , apple ...etc
    public BaseResponse<JwtToken> socialLogin(@RequestParam String code, @PathVariable String registrationId, HttpServletResponse response) throws BaseException, IOException {
        JwtToken jwt;
        if(registrationId.equals("google"))
            jwt = oAuthService.googleLogin(code, registrationId,response);
        else if(registrationId.equals("kakao"))
            jwt = oAuthService.kakaoLogin(code, registrationId,response);
        else if(registrationId.equals("apple"))
            jwt = oAuthService.appleLogin(code,registrationId,response);
        else {
            throw new BaseException(BAD_ACCESS);
        }

        return new BaseResponse<>(jwt);
    }

    @GetMapping("/login")
    public BaseResponse<Void> loginOnlySocialServer(){
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }
}
