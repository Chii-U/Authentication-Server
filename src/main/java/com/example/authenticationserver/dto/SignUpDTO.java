package com.example.authenticationserver.dto;

import java.time.LocalDate;

public record SignUpDTO(String username, String password, String real_name, String email, boolean gender, LocalDate birthday) {
}
