package com.example.authenticationserver.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Builder@Getter@Setter
@RedisHash(value = "refresh")
public class RefreshToken {
    @Id
    private String token;
    private String username;
    @TimeToLive
    private long ttl;
}
