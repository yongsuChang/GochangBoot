package kr.co.gochang.api.dto;

/** 목록용. 본문은 싣지 않는다 (목록 화면은 본문을 쓰지 않는다). */
public record ContentSummary(
        Long id,
        Long number,
        String title,
        String writeDate,
        String writer,
        String count,
        String recommend,
        String replyCount,
        String picture
) {}
