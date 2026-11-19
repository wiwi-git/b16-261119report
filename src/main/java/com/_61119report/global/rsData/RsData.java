package com._61119report.global.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 공통 응답 래퍼. resultCode 는 "{HTTP 상태코드}-{세부코드}" 형식 (예: "201-1").
 * statusCode 는 resultCode 앞부분에서 계산되며 응답 JSON 에는 포함되지 않는다.
 */
public record RsData<T>(
        String resultCode,
        @JsonIgnore int statusCode,
        String msg,
        T data
) {

    public RsData(String resultCode, String msg, T data) {
        this(resultCode, Integer.parseInt(resultCode.split("-", 2)[0]), msg, data);
    }

    public RsData(String resultCode, String msg) {
        this(resultCode, msg, null);
    }
}
