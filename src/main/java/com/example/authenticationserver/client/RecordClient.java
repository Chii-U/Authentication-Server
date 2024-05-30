package com.example.authenticationserver.client;


import com.example.authenticationserver.config.MultiPartConfig;
import com.example.authenticationserver.dto.PainRecordDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@FeignClient(name = "record", url = "${spring.cloud.openfeign.client.record-url}",configuration = MultiPartConfig.class)
@Qualifier("record")
public interface RecordClient {
    @GetMapping("/pain-records/{username}")
    public Map<String,Object> getAllPainRecords(@PathVariable String username);

    // 특정 날짜의 통증 기록 조회
    @GetMapping("/pain-records/{username}/date")
    public Map<String,Object> getPainRecordByDate(@PathVariable String username, @RequestBody LocalDateTime painTimestamp);
    // 통증 기록 등록
    @PostMapping("/pain-records/post")
    public Map<String,Object> addPainRecord(@RequestBody PainRecordDto painRecordDto);
    // 통증 기록 수정
    @PostMapping("/pain-records/update/{id}")
    public Map<String,Object> updatePainRecord(@PathVariable String id, @RequestBody PainRecordDto updatedPainRecord);

    // 통증 기록 삭제
    @PostMapping("/pain-records/delete/{id}")
    public Map<String,Object> deletePainRecord(@PathVariable String id);

}
