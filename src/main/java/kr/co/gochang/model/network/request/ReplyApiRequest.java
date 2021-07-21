package kr.co.gochang.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyApiRequest {

    private String contentNumber;

    private String number;

    private String writeDate;

    private String writer;

    private String content;

    private String isReReply;

    private String indexInContent;
}
