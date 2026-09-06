package kr.co.gochang.domain;

import java.util.Optional;

public enum SearchType {
    TITLE, CONTENT, WRITER;

    /** 쿼리 파라미터 값("title" 등)을 대소문자 구분 없이 매핑한다. */
    public static Optional<SearchType> fromParam(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(value.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
