package com.example.authenticationserver.entity;


import com.example.authenticationserver.enumerated.Authority;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// 데이터베이스에 접근하는 엔티티는 아닙니다.
// db 접근을 줄이고자 Jwt를 사용할 때 필요한 적당한 정보만 저장된 객체!
@Getter
public class Account implements UserDetails {
    private String username;
    private String password;
    private Collection<GrantedAuthority> authorities;
    private boolean isEnabled;
    private boolean isAccountNonLocked;
    private boolean isAccountNonExpired;
    private boolean isCredentialsNonExpired;

    public Account(String username, String password, Collection<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

}
