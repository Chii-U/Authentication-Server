package com.example.authenticationserver.controller.feign;

import com.example.authenticationserver.client.RecordClient;
import com.example.authenticationserver.dto.PainRecordDto;
import com.example.authenticationserver.entity.Account;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.global.BaseResponse;
import com.example.authenticationserver.global.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pain-records")
public class RecordController {
    @Autowired
    RecordClient recordClient;
    @GetMapping("/view")
    public BaseResponse<Object> getAllPainRecords(@AuthenticationPrincipal Account account) {
        Map<String,Object> res = recordClient.getAllPainRecords(account.getUsername());
        return new BaseResponse<Object>(res.getOrDefault("data","nodata"));

    }

    // 특정 날짜의 통증 기록 조회
    @GetMapping("/view/date")
    public BaseResponse<Object> getPainRecordByDate(@AuthenticationPrincipal Account account, @RequestBody LocalDateTime painTimestamp) {
        Map<String,Object> res = recordClient.getPainRecordByDate(account.getUsername(),painTimestamp);
        return new BaseResponse<Object>(res.getOrDefault("data","nodata"));
    }

    // 통증 기록 등록
    @PostMapping("/post")
    public BaseResponse<Object> addPainRecord(@AuthenticationPrincipal Account account, @RequestBody PainRecordDto painRecordDto) {
        painRecordDto.setUsername(account.getUsername());
        Map<String,Object> res = recordClient.addPainRecord(painRecordDto);
        return new BaseResponse<Object>(res.getOrDefault("data","nodata"));
    }

    // 통증 기록 수정
    @PostMapping("/update/{id}")
    public BaseResponse<Object> updatePainRecord(@AuthenticationPrincipal Account account,@PathVariable String id, @RequestBody PainRecordDto updatedPainRecord) {
        updatedPainRecord.setUsername(account.getUsername());
        Map<String,Object> res = recordClient.updatePainRecord(id,updatedPainRecord);
        return new BaseResponse<Object>(res.getOrDefault("data","nodata"));
    }

    // 통증 기록 삭제
    // sy : 누구든 id를 안다면 해당 기록을 삭제할 수 있는 취약성이 있을 것 같다.
    // 일단 로그인ㄴ되지 않은 유저는 처리못하게 1차방어
    @PostMapping("/delete/{id}")
    public BaseResponse<Object> deletePainRecord(@AuthenticationPrincipal Account account,@PathVariable String id) throws BaseException {
        if(account == null) {
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        }
        Map<String,Object> res = recordClient.deletePainRecord(id);
        return new BaseResponse<Object>(res.getOrDefault("data","nodata"));
    }
}
