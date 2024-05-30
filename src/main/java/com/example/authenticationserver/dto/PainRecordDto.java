package com.example.authenticationserver.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor@Getter@Setter
public class PainRecordDto {
    private String id;
    private String username;
    private String location;
    private String trigger;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime painTimestamp;
    private List<String> type;
    private Integer intensity;
}