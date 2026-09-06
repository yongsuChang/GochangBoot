package kr.co.gochang.service;

import kr.co.gochang.api.dto.ContentDetail;
import kr.co.gochang.api.dto.ContentSummary;
import kr.co.gochang.api.dto.Neighbors;
import kr.co.gochang.domain.ContentRepository;
import kr.co.gochang.domain.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 데이터가 절대 바뀌지 않는 아카이브이므로 모든 조회 결과를 무기한 캐시한다.
 * 캐시 크기 상한은 application.properties 의 spring.cache.caffeine.spec 참고.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    public static final String CACHE_LIST = "contentList";
    public static final String CACHE_SEARCH = "contentSearch";
    public static final String CACHE_DETAIL = "contentDetail";
    public static final String CACHE_TITLE = "contentTitle";
    public static final String CACHE_NEIGHBORS = "contentNeighbors";

    private final ContentRepository contentRepository;

    @Cacheable(CACHE_LIST)
    public Page<ContentSummary> list(Pageable pageable) {
        return contentRepository.findSummaries(pageable);
    }

    @Cacheable(CACHE_SEARCH)
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

    @Cacheable(CACHE_DETAIL)
    public Optional<ContentDetail> get(Long id) {
        return contentRepository.findById(id).map(ContentDetail::from);
    }

    @Cacheable(CACHE_TITLE)
    public Optional<String> title(Long id) {
        return Optional.ofNullable(contentRepository.findTitleById(id));
    }

    /** 상세 페이지의 앞뒤 글 링크. 삭제된 글은 건너뛴다. */
    @Cacheable(CACHE_NEIGHBORS)
    public Neighbors neighbors(Long id) {
        return new Neighbors(
                contentRepository.findFirstByIdGreaterThanOrderByIdAsc(id).map(this::link).orElse(null),
                contentRepository.findFirstByIdLessThanOrderByIdDesc(id).map(this::link).orElse(null));
    }

    private Neighbors.Link link(kr.co.gochang.domain.Content c) {
        return new Neighbors.Link(c.getId(), c.getTitle());
    }
}
