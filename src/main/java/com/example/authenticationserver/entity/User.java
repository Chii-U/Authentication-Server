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
    String objectId; // 이걸로 인증하지 않고 username으로함 이거는 그냥 일련번호로 활용하기 위함.
    String username;
    @Field(name = "real_name")
    String realName;
    String password;
    String email;
    @DateTimeFormat
    LocalDate birthday;
    Boolean gender;
    @Field(name = "agree_marketing")
    Boolean agreeMarketing;
    @Field(name = "join_date")
    LocalDateTime joinDate;

    @Field(name = "drop_date")
    LocalDateTime dropDate;


    //시큐리티 관련 필요한 것들
    @Field(name = "is_account_nonlocked")
    private Boolean isAccountNonLocked;
    @Field(name = "is_enabled")
    boolean isEnabled;

    private String provider;
    private String providerId;


    @Enumerated(EnumType.STRING)
    Authority authority;


}
