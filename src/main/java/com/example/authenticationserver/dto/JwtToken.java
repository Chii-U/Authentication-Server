package com.example.authenticationserver.dto;

import lombok.*;

@Builder@Setter@Data // DATA Getter / setter 명시 없을 경우 type definition error 가능성
@AllArgsConstructor
public class JwtToken{
    String grantType;
    String accessToken;
}