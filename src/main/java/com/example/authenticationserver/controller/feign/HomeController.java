package com.example.authenticationserver.controller.feign;

import com.example.authenticationserver.client.HomeClient;
import com.example.authenticationserver.dto.PainRecordDto;
import com.example.authenticationserver.dto.VideoDto;
import com.example.authenticationserver.entity.Account;
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
            BaseResponse<List<PainRecordDto>> res = homeClient.getPainRecordByDate(username, painDate);
            if (res.getResult() != null && !res.getResult().isEmpty()) {
                return res;
            } else {
                return new BaseResponse<>(BaseResponseStatus.NO_DATA_FOUND); // 빈 리스트 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR); // 내부 서버 오류 처리
        }
    }

    // 특정 질병명에 따른 운동 비디오 조회
    @GetMapping("/exercise/{diseaseName}")
    public BaseResponse<List<VideoDto>> getExerciseByDiseaseName(@AuthenticationPrincipal Account account, @PathVariable("diseaseName") String diseaseName) {
        if (account == null) {
            return new BaseResponse<>(BaseResponseStatus.LOGIN_FIRST);  // 인증되지 않은 경우 처리
        }
        try {
            BaseResponse<List<VideoDto>> videos = homeClient.getExerciseByDiseaseName(account.getUsername(), diseaseName);
            if (videos.getResult() != null && !videos.getResult().isEmpty()) {
                return videos;
            } else {
                return new BaseResponse<>(BaseResponseStatus.NO_VIDEOS_FOUND);  // 비디오가 없을 경우 처리
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);  // 내부 서버 오류 처리
        }
    }

    // 제외시킬 운동 목록 조회
    @GetMapping("/excludedExercise")
    public BaseResponse<List<String>> getExcludedExercise(@AuthenticationPrincipal Account account) {
        if (account == null) {
            return new BaseResponse<>(BaseResponseStatus.LOGIN_FIRST);  // 인증되지 않은 경우 처리
        }
        try {
            BaseResponse<List<String>> excludedExercises = homeClient.getExcludedExercise(account.getUsername());
            if (excludedExercises.getResult() != null && !excludedExercises.getResult().isEmpty()) {
                return excludedExercises;  // 성공적으로 제외 운동 목록 반환
            } else {
                return new BaseResponse<>(BaseResponseStatus.NO_EXCLUDED_EXERCISES_FOUND);  // 제외할 운동이 없을 경우 처리
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);  // 내부 서버 오류 처리
        }
    }
}
