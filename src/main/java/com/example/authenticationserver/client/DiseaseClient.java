package com.example.authenticationserver.client;

import com.example.authenticationserver.config.MultiPartConfig;
import com.example.authenticationserver.dto.DiseaseRequestDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "disease", url = "${spring.cloud.openfeign.client.disease-url}", configuration = MultiPartConfig.class)
@Qualifier("disease")
public interface DiseaseClient {

    @PostMapping(value = "/disease", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    Object predictDisease(@RequestBody DiseaseRequestDto req);

    @GetMapping(value = "/disease", produces = APPLICATION_JSON_VALUE)
    Object getDisease(@RequestParam("username") String username);
}
