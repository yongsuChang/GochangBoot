package kr.co.gochang.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyApiResponse {

    private String contentNumber;

    private String number;

    private String writeDate;

    private String writer;

    private String content;

    private String isReReply;

    private String indexInContent;
}
