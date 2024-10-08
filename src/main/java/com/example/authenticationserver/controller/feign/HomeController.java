package com.example.authenticationserver.controller.feign;

import com.example.authenticationserver.client.HomeClient;
import com.example.authenticationserver.dto.PainRecordDto;
import com.example.authenticationserver.dto.VideoDto;
import com.example.authenticationserver.entity.Account;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.global.BaseResponse;
import com.example.authenticationserver.global.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeController {

    @Autowired
    private HomeClient homeClient;

    // 특정 날짜의 통증 기록 조회
    @GetMapping("/{username}/date")
    public BaseResponse<List<PainRecordDto>> getPainRecordByDate(@PathVariable String username, @RequestParam LocalDate painDate) {
        try {
            List<PainRecordDto> res = homeClient.getPainRecordByDate(username, painDate);
            if (res != null && !res.isEmpty()) {
                return new BaseResponse<>(res);  // 성공적으로 데이터를 반환
            } else {
                return new BaseResponse<>(BaseResponseStatus.NO_DATA_FOUND);  // 데이터가 없을 경우 요청 실패로 처리
            }
        } catch (Exception e) {
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);  // 내부 서버 오류로 처리
        }
    }

    // 특정 질병명에 따른 운동 비디오 조회
    @GetMapping("/exercise/{diseaseName}")
    public BaseResponse<List<VideoDto>> getExerciseByDiseaseName(@AuthenticationPrincipal Account account, @PathVariable("diseaseName") String diseaseName) {
        if (account == null) {
            return new BaseResponse<>(BaseResponseStatus.LOGIN_FIRST);  // 인증되지 않은 경우 예외 처리
        }
        try {
            List<VideoDto> videos = homeClient.getExerciseByDiseaseName(account.getUsername(), diseaseName);
            if (videos != null && !videos.isEmpty()) {
                return new BaseResponse<>(videos);  // 성공적으로 비디오 데이터를 반환
            } else {
                return new BaseResponse<>(BaseResponseStatus.NO_DATA_FOUND);  // 비디오가 없을 경우 요청 실패로 처리
            }
        } catch (Exception e) {
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);  // 내부 서버 오류로 처리
        }
    }

    // 제외시킬 운동 목록 조회
    @GetMapping("/excludedExercise")
    public BaseResponse<List<String>> getExcludedExercise(@AuthenticationPrincipal Account account) {
        if (account == null) {
            return new BaseResponse<>(BaseResponseStatus.LOGIN_FIRST);  // 인증되지 않은 경우 예외 처리
        }
        try {
            List<String> excludedExercises = homeClient.getExcludedExercise(account.getUsername());
            if (excludedExercises != null && !excludedExercises.isEmpty()) {
                return new BaseResponse<>(excludedExercises);  // 성공적으로 제외 운동 목록 반환
            } else {
                return new BaseResponse<>(BaseResponseStatus.NO_DATA_FOUND);  // 제외할 운동이 없을 경우 요청 실패로 처리
            }
        } catch (Exception e) {
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);  // 내부 서버 오류로 처리
        }
    }
}

