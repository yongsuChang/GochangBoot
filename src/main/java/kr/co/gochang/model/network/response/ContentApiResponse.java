package kr.co.gochang.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentApiResponse {

    private Long id;

    private String number;

    private String title;

    private String writeDate;

    private String writer;

    private String count;

    private String recommend;

    private String replyCount;

    private String content;

    private String picture;
}
