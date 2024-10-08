package com.example.authenticationserver.dto;

import lombok.*; 

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class DiseaseRequestDto {
    String username;
    String disease;
}
