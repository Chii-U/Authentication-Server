package com.example.authenticationserver.entity;

import com.example.authenticationserver.enumerated.Authority;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Document(collection = "user")
@Getter@AllArgsConstructor
@Setter@Data @Builder
public class User {
    @Id
    String username;
    @Field(name = "real_name")
    String realName;
    String password;
    String email;
    LocalDate birthday;
    boolean gender;
    @Field(name = "join_date")
    Timestamp joinDate;


    //시큐리티 관련 필요한 것들
    @Field(name = "is_account_nonlocked")
    private boolean isAccountNonLocked;
    @Field(name = "is_enabled")
    boolean isEnabled;


    @Enumerated(EnumType.STRING)
    Authority authority;


}
