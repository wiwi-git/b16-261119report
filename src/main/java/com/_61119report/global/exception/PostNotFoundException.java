package com._61119report.global.exception;

/**
 * 존재하지 않는 글 조회/수정/삭제 → GlobalExceptionHandler 에서 404 처리.
 */
public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(Long id) {
        super("%d번 글은 존재하지 않습니다.".formatted(id));
    }
}
