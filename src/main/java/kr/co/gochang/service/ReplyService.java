package kr.co.gochang.service;

import kr.co.gochang.api.dto.ReplyResponse;
import kr.co.gochang.domain.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    public static final String CACHE_REPLIES = "replies";

    private final ReplyRepository replyRepository;

    @Cacheable(CACHE_REPLIES)
    public Page<ReplyResponse> byContentId(Long contentId, Pageable pageable) {
        return replyRepository.findByContentId(contentId, pageable).map(ReplyResponse::from);
    }
}
