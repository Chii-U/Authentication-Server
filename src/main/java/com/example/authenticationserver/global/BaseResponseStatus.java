package com.example.authenticationserver.global;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    /**
     * 2000 : 요청 성공
     */
    SUCCESS(true, 2000, "요청에 성공하였습니다."),
    DUPLICATION(false, 2001, "중복이 있습니다"),


    /**
     * 4000 : Request 오류 채워 넣어야지 이제.
     */
    // Common

    FAILED(false, 4000, "요청에 실패하였습니다."),
    PASSWORD_NOT_MATCH(false, 4001, "패스워드 틀려"),
    LOGIN_EXPIRED(false,4002, "인증이 만료되었습니다. 다시 로그인해주세요."),
    USER_NOT_EXISTS(false,4003,"회원 정보가 존재하지 않습니다."),
    EXISTS_USERNAME(false, 4004, "이미 존재하는 회원 정보입니다."),
    CODE_NOT_MATCH(false, 4005, "인증 코드가 올바르지 않습니다"),
    UNABLE_TO_SEND_EMAIL(false, 4006, "이메일을 발송하지 못했습니다."),
    BAD_ACCESS(false, 4007, "잘못된 요청입니다."),
    LOGIN_FIRST(false, 4008, "먼저 로그인이 필요합니다.");
    ;





    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
