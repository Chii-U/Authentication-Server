package com.example.authenticationserver.config;

import com.example.authenticationserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfiguration {
    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 0 0 ? * 4")
    public void run() {
        userService.hardDeleteDate();
    }
}