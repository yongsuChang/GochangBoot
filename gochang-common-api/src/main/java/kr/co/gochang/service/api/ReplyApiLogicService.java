package kr.co.gochang.service.api;

import kr.co.gochang.model.entity.Content;
import kr.co.gochang.model.entity.Reply;
import kr.co.gochang.model.network.Header;
import kr.co.gochang.model.network.request.ReplyApiRequest;
import kr.co.gochang.model.network.response.ReplyApiResponse;
import kr.co.gochang.repository.ContentRepository;
import kr.co.gochang.repository.ReplyRepository;
import kr.co.gochang.service.BaseService;
import kr.co.gochang.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplyApiLogicService extends BaseService<ReplyApiRequest, ReplyApiResponse, Reply> {
    @Autowired
    ContentRepository contentRepository;

    @Override
    public ReplyApiResponse response(Reply reply) {
        return ReplyApiResponse.builder()
                .id(reply.getId())
                .writeDate(reply.getWriteDate())
                .writer(reply.getWriter())
                .content(reply.getContent())
                .isReReply(reply.getIsReReply())
                .indexInContent(reply.getIndexInContent())
                .contentId(reply.getContentId())
                .build();
    }

    @Override
    public Header<ReplyApiResponse> create(Header<ReplyApiRequest> request) {
        return null;
    }

    @Override
    public Header<ReplyApiResponse> read(Long id) {
        return null;
    }

    @Override
    public Header<ReplyApiResponse> update(Header<ReplyApiRequest> request) {
        return null;
    }

    @Override
    public Header delete(Long id) {
        return null;
    }

    public Header<List<ReplyApiResponse>> readReplyListsByContentId(
            Pageable pageable, Long contentId){
        ReplyRepository replyRepository = (ReplyRepository)baseRepository;

        // Content의 id와 number가 달라서 id로 number 조회해야. 안 좋은 구조
        String number = contentRepository.findById(contentId)
            .map(Content::getNumber)
            .orElseGet(() -> "0");

        Page<Reply> replyPage = replyRepository.findAllByContentId(pageable, Long.valueOf(number));
        return PaginationUtils.getPaginationHeader(replyPage, this);
    }
}
