package com.example.authenticationserver.controller.feign;
import com.example.authenticationserver.client.GuideClient;
import com.example.authenticationserver.client.PredictClient;
import com.example.authenticationserver.dto.ExcludeExerciseDto;
import com.example.authenticationserver.dto.UsernameDto;
import com.example.authenticationserver.entity.Account;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/videos")
public class GuideController {
    @Autowired
    private GuideClient guideClient;
    @PostMapping(value="/user/excludeExercise")
    public ResponseEntity<String> excludeExercise(@AuthenticationPrincipal Account account, @RequestBody ExcludeExerciseDto excludeExerciseDto) {
        excludeExerciseDto.setUsername(account.getUsername());
        try {
            ResponseEntity<String> response = guideClient.excludeExercise(excludeExerciseDto);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return new ResponseEntity<>(response.getBody(), HttpStatus.OK); // 변경된 상태 코드
            } else {
                return new ResponseEntity<>("Failed to exclude exercise.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred while excluding exercise: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}