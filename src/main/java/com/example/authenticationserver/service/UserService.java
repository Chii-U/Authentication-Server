package com.example.authenticationserver.service;

import com.example.authenticationserver.entity.User;
import com.example.authenticationserver.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service@Slf4j
public class UserService {
    @Autowired
    UserRepository userRepository;
    public User findByUsernameAndIsEnabledTrue(String username){
        return userRepository.findByUsernameAndIsEnabledTrue(username).orElseThrow();
    }

}
