package com.example.authenticationserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public record JwtToken (String grantType, String accessToken){}