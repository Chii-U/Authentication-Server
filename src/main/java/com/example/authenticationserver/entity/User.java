package com.example.authenticationserver.entity;

import com.example.authenticationserver.enumerated.Authority;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @DateTimeFormat
    LocalDate birthday;
    boolean gender;
    @Field(name = "join_date")
    LocalDateTime joinDate;


    //시큐리티 관련 필요한 것들
    @Field(name = "is_account_nonlocked")
    private boolean isAccountNonLocked;
    @Field(name = "is_enabled")
    boolean isEnabled;


    @Enumerated(EnumType.STRING)
    Authority authority;


}
