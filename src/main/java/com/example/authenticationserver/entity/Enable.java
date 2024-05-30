package com.example.authenticationserver.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Builder@Getter@Setter
@RedisHash(value = "enable")
public class Enable {
    @Id
    private String enableId;
    @Indexed
    private String authNumber;
    private String email;
    @TimeToLive
    private long ttl;
}
