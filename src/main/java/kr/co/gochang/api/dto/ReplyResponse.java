package kr.co.gochang.api.dto;

import kr.co.gochang.domain.Reply;

public record ReplyResponse(
        Long id,
        String writeDate,
        String writer,
        String content,
        String isReReply,
        Integer indexInContent,
        Long contentId
) {
    public static ReplyResponse from(Reply r) {
        return new ReplyResponse(r.getId(), r.getWriteDate(), r.getWriter(), r.getContent(),
                r.getIsReReply(), r.getIndexInContent(), r.getContentNumber());
    }
}
