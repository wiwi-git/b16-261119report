package com._61119report.global.exception;

/**
 * 회원가입 시 이미 사용 중인 username 이 입력된 경우 → 409.
 */
public class MemberDuplicateUsernameException extends RuntimeException {

    public MemberDuplicateUsernameException(String username) {
        super("%s(은)는 이미 사용중인 username 입니다.".formatted(username));
    }
}
