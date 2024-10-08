package com.example.authenticationserver.controller.feign;

import com.example.authenticationserver.client.DiseaseClient;
import com.example.authenticationserver.dto.DiseaseRequestDto;
import com.example.authenticationserver.entity.Account;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.authenticationserver.global.BaseResponseStatus.LOGIN_FIRST;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DiseaseController {

    @Autowired
    private DiseaseClient diseaseClient;
    
    @PostMapping("/disease")
    public BaseResponse<Object> predictDisease(
            @AuthenticationPrincipal Account account,
            DiseaseRequestDto dto // DTO로 변경하여 요청 본문에서 사용자 이름과 질병 정보를 받음
    ) throws BaseException {
        if (account == null) {
            throw new BaseException(LOGIN_FIRST);
        }

        dto.setUsername(account.getUsername()); // 인증된 사용자 이름 설정

        // 외부 API 호출
        Object response = diseaseClient.predictDisease(dto);

        return new BaseResponse<>(response);
    }

    @GetMapping("/disease")
    public BaseResponse<Object> getDisease(
            @AuthenticationPrincipal Account account, // 인증된 사용자
            @RequestBody Map<String, String> requestBody // 요청 본문에서 JSON을 받을 수 있도록 설정
    ) throws BaseException {
        if (account == null) {
            throw new BaseException(LOGIN_FIRST); // 인증되지 않은 사용자 처리
        }

        String username = requestBody.get("username"); // 요청 본문에서 사용자 이름 가져오기

        // 외부 API 호출
        Object response = diseaseClient.getDisease(username);

        return new BaseResponse<>(response); // 응답 반환
    }
}
