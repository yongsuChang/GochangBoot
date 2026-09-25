package kr.co.gochang.service;

import kr.co.gochang.config.CacheNames;
import kr.co.gochang.domain.Content;
import kr.co.gochang.domain.SearchType;
import kr.co.gochang.dto.response.ContentDetail;
import kr.co.gochang.dto.response.ContentSummary;
import kr.co.gochang.dto.response.Neighbors;
import kr.co.gochang.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 데이터가 절대 바뀌지 않는 아카이브이므로 모든 조회 결과를 캐시한다.
 * 캐시 크기 상한은 application.properties 의 spring.cache.caffeine.spec 참고.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;

    @Cacheable(CacheNames.CONTENT_LIST)
    public Page<ContentSummary> list(Pageable pageable) {
        return contentRepository.findSummaries(pageable);
    }

    @Cacheable(CacheNames.CONTENT_SEARCH)
    public Page<ContentSummary> search(SearchType type, String word, Pageable pageable) {
        String trimmed = word.trim();
        if (trimmed.isEmpty()) {
            return contentRepository.findSummaries(pageable);
        }
        return switch (type) {
            case TITLE -> contentRepository.searchByTitle(trimmed, pageable);
            case WRITER -> contentRepository.searchByWriter(trimmed, pageable);
            case CONTENT -> contentRepository.searchByContent(trimmed, pageable);
        };
    }

    @Cacheable(CacheNames.CONTENT_DETAIL)
    public Optional<ContentDetail> get(Long id) {
        return contentRepository.findById(id).map(ContentDetail::from);
    }

    @Cacheable(CacheNames.CONTENT_TITLE)
    public Optional<String> title(Long id) {
        return Optional.ofNullable(contentRepository.findTitleById(id));
    }

    /** 상세 페이지의 앞뒤 글 링크. 삭제된 글은 건너뛴다. */
    @Cacheable(CacheNames.CONTENT_NEIGHBORS)
    public Neighbors neighbors(Long id) {
        return new Neighbors(
                contentRepository.findFirstByIdGreaterThanOrderByIdAsc(id).map(this::link).orElse(null),
                contentRepository.findFirstByIdLessThanOrderByIdDesc(id).map(this::link).orElse(null));
    }

    private Neighbors.Link link(Content c) {
        return new Neighbors.Link(c.getId(), c.getTitle());
    }
}
