package com._61119report.global.exception;

/**
 * 존재하지 않거나 해당 글에 속하지 않은 댓글 → GlobalExceptionHandler 에서 404 처리.
 */
public class PostCommentNotFoundException extends RuntimeException {

    public PostCommentNotFoundException(Long id) {
        super("%d번 댓글은 존재하지 않습니다.".formatted(id));
    }
}
