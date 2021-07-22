package kr.co.gochang.model.enumclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchType {
    TITLE("title"),
    CONTENT("content"),
    WRITER("writer"),
    NONE("none")
    ;

    private final String value;
}
