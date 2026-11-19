package com._61119report.global.exception;

/**
 * 글/댓글 수정·삭제 시 actorId 가 작성자가 아닌 경우 → 403.
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException() {
        super("권한이 없습니다.");
    }
}
