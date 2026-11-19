package com._61119report.global.exception;

/**
 * actorId 가 필요한 액션에서 actorId 파라미터가 없는 경우 → 401.
 */
public class UnauthenticatedException extends RuntimeException {

    public UnauthenticatedException() {
        super("로그인 후 이용해주세요.");
    }
}
