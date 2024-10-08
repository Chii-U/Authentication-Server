package com.example.authenticationserver.controller.feign;

import com.example.authenticationserver.client.DiseaseClient;
import com.example.authenticationserver.dto.DiseaseRequestDto;
import com.example.authenticationserver.entity.Account;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.authenticationserver.global.BaseResponseStatus.LOGIN_FIRST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/disease")
public class DiseaseController {

    @Autowired
    private DiseaseClient diseaseClient;

    @PostMapping("")
    public BaseResponse<Object> predictDisease(
            @AuthenticationPrincipal Account account,
            @RequestBody DiseaseRequestDto dto // DTO로 요청 본문 받기
    ) throws BaseException {
        if (account == null) {
            throw new BaseException(LOGIN_FIRST);
        }

        dto.setUsername(account.getUsername()); // 인증된 사용자 이름 설정

        // 외부 API 호출
        Object response = diseaseClient.predictDisease(dto);

        return new BaseResponse<>(response);
    }

    @GetMapping("")
    public BaseResponse<Object> getDisease(
            @AuthenticationPrincipal Account account, // 인증된 사용자
            @RequestParam("username") String username // @RequestParam으로 사용자 이름 받기
    ) throws BaseException {
        if (account == null) {
            throw new BaseException(LOGIN_FIRST); // 인증되지 않은 사용자 처리
        }

        // 외부 API 호출
        Object response = diseaseClient.getDisease(username);

        return new BaseResponse<>(response); // 응답 반환
    }
}
