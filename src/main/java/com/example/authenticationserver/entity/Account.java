package com.example.authenticationserver.entity;


import com.example.authenticationserver.enumerated.Authority;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

// 데이터베이스에 접근하는 엔티티는 아닙니다.
// db 접근을 줄이고자 Jwt를 사용할 때 필요한 적당한 정보만 저장된 객체!
// 쓰다 안쓰다 oauth때문에 어짜피 필요해서... 다시생성...
@Getter
public class Account implements UserDetails, OAuth2User {

    public Account(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public Account(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // ???
    @Override
    public String getName() {
        return user.username;
    }

    private User user;
    private Map<String, Object> attributes;



    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean isEnabled = true;
    private boolean isAccountNonLocked = true;
    private boolean isAccountNonExpired = true;
    private boolean isCredentialsNonExpired = true;

}
