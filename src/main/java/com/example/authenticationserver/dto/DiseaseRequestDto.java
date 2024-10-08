package com.example.authenticationserver.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseRequestDto {
    private String username;
    private String disease;
}
