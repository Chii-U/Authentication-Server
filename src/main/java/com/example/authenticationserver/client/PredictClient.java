package com.example.authenticationserver.client;


import com.example.authenticationserver.config.MultiPartConfig;
import com.example.authenticationserver.dto.UsernameDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "predict", url = "${spring.cloud.openfeign.client.predict-url}",configuration = MultiPartConfig.class)
@Qualifier("predict")
public interface PredictClient {

    @PostMapping(value = "/ai/predict",produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    Object predictDisease(
            @RequestBody UsernameDto req);
}
