package com.example.authenticationserver.enumerated;

import org.springframework.security.core.GrantedAuthority;

// 추 후 확장성을 고려하여 롤을 두개로 만들어놓음, 기본 = PATIENT
public enum Authority implements GrantedAuthority {
    ROLE_PATIENT, ROLE_DOCTOR;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
