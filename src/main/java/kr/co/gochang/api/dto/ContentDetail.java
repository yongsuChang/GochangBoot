package kr.co.gochang.api.dto;

import kr.co.gochang.domain.Content;

public record ContentDetail(
        Long id,
        Long number,
        String title,
        String writeDate,
        String writer,
        String count,
        String recommend,
        String replyCount,
        String content,
        String picture
) {
    public static ContentDetail from(Content c) {
        return new ContentDetail(c.getId(), c.getNumber(), c.getTitle(), c.getWriteDate(), c.getWriter(),
                c.getCount(), c.getRecommend(), c.getReplyCount(), c.getContent(), c.getPicture());
    }
}
