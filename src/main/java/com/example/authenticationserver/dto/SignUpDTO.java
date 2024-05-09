package com.example.authenticationserver.dto;

import java.time.LocalDate;

public record SignUpDTO(String username, String password, String realName, String email, boolean gender, LocalDate birthday, boolean agreeMarketing) {
}
