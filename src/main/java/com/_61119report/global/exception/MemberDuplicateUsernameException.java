package com._61119report.global.exception;

/**
 * 회원가입 시 이미 사용 중인 username 이 입력된 경우 → 409.
 */
public class MemberDuplicateUsernameException extends RuntimeException {

    public MemberDuplicateUsernameException(String username) {
        super("%s 는 이미 사용 중인 아이디입니다.".formatted(username));
    }
}
