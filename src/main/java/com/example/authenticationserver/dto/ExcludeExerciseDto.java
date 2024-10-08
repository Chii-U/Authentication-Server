package com.example.authenticationserver.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
@Data
@NoArgsConstructor // 기본 생성자 추가
public class ExcludeExerciseDto {
    private ObjectId id;
    private String username;
    private String exerciseName;
}