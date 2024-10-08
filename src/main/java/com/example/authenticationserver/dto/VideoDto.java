package com.example.authenticationserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class VideoDto {
    private List<String> exerciseNames;
    private List<String> filenames;
    private List<Integer> times;
    private List<String> videoUrls; // 비디오 URL 필드 추가
    private String diseaseName;
    private String totalTime;
    private int exerciseCount;
}
