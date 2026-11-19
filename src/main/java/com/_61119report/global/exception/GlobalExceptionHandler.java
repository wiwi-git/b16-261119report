package com._61119report.global.exception;

import com._61119report.global.rsData.RsData;
import java.util.stream.Collectors;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 모든 예외를 RsData 로 변환한다. HTTP 상태코드는 ResponseAspect 가 rsData.statusCode() 로 지정한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: @Valid 검증 실패 → "필드-코드-메시지" 를 필드명 순으로 정렬해 줄바꿈으로 연결
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RsData<Void> handle(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .map(error -> error.getField() + "-" + error.getCode() + "-" + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("\n"));

        return new RsData<>("400-1", message);
    }

    // 400: 요청 본문(JSON)을 읽을 수 없음
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public RsData<Void> handle(HttpMessageNotReadableException e) {
        return new RsData<>("400-2", "요청 본문이 올바르지 않습니다.");
    }

    // 401: actorId 가 필요한 액션에서 actorId 누락
    @ExceptionHandler(UnauthenticatedException.class)
    public RsData<Void> handle(UnauthenticatedException e) {
        return new RsData<>("401-1", e.getMessage());
    }

    // 403: 작성자가 아닌 사용자의 수정/삭제
    @ExceptionHandler(AccessDeniedException.class)
    public RsData<Void> handle(AccessDeniedException e) {
        return new RsData<>("403-1", e.getMessage());
    }

    // 404: 글 없음
    @ExceptionHandler(PostNotFoundException.class)
    public RsData<Void> handle(PostNotFoundException e) {
        return new RsData<>("404-1", e.getMessage());
    }

    // 404: 댓글 없음
    @ExceptionHandler(PostCommentNotFoundException.class)
    public RsData<Void> handle(PostCommentNotFoundException e) {
        return new RsData<>("404-2", e.getMessage());
    }

    // 409: 회원가입 username 중복
    @ExceptionHandler(MemberDuplicateUsernameException.class)
    public RsData<Void> handle(MemberDuplicateUsernameException e) {
        return new RsData<>("409-1", e.getMessage());
    }
}
