package com.example.authenticationserver.client;

import com.example.authenticationserver.config.MultiPartConfig;
import com.example.authenticationserver.dto.PainRecordDto;
import com.example.authenticationserver.dto.VideoDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "home", url = "${spring.cloud.openfeign.client.home-url}", configuration = MultiPartConfig.class)
@Qualifier("home")
public interface HomeClient {

    // 특정 날짜의 통증 기록 조회
    @GetMapping("/home/{username}/date")
    List<PainRecordDto> getPainRecordByDate(@PathVariable String username, @RequestParam("painDate") LocalDate painDate);

    // 특정 질병명에 따른 운동 비디오 조회
    @GetMapping("/home/exercise/{username}/{diseaseName}")
    List<VideoDto> getExerciseByDiseaseName(@PathVariable("username") String username, @PathVariable("diseaseName") String diseaseName);

    // 제외시킬 운동 목록 조회
    @GetMapping("/home/{username}/excludedExercise")
    List<String> getExcludedExercise(@PathVariable String username);
}
