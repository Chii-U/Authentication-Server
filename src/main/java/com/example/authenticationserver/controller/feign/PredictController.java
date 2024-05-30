package com.example.authenticationserver.controller.feign;

import com.example.authenticationserver.client.PredictClient;
import com.example.authenticationserver.dto.UsernameDto;
import com.example.authenticationserver.entity.Account;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.example.authenticationserver.global.BaseResponseStatus.LOGIN_FIRST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai")
public class PredictController {

    @Autowired
    private PredictClient predictClient;

    @PostMapping("/predict")
    public BaseResponse<Object> predictDisease(
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        UsernameDto dto = new UsernameDto(account.getUsername());

        return new BaseResponse<>(predictClient.predictDisease(dto));
    }
}
