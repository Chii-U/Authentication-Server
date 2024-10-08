package com.example.authenticationserver.client;
import com.example.authenticationserver.config.MultiPartConfig;
import com.example.authenticationserver.dto.ExcludeExerciseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
@FeignClient(name = "guide", url = "${spring.cloud.openfeign.client.guide-url}", configuration = MultiPartConfig.class)
@Qualifier("guide")
public interface GuideClient {
    @PostMapping("/videos/user/excludeExercise")
    ResponseEntity<String> excludeExercise(@RequestBody ExcludeExerciseDto excludeExerciseDto);
}