package kr.co.gochang.domain;

import kr.co.gochang.api.dto.ContentSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long> {

    String SUMMARY = "select new kr.co.gochang.api.dto.ContentSummary("
            + "c.id, c.number, c.title, c.writeDate, c.writer, c.count, c.recommend, c.replyCount, c.picture) "
            + "from Content c ";

    /** 목록은 본문(CLOB)을 읽지 않는 요약 프로젝션만 사용한다. */
    @Query(SUMMARY)
    Page<ContentSummary> findSummaries(Pageable pageable);

    @Query(SUMMARY + "where lower(c.title) like lower(concat('%', :word, '%'))")
    Page<ContentSummary> searchByTitle(@Param("word") String word, Pageable pageable);

    @Query(SUMMARY + "where lower(c.writer) like lower(concat('%', :word, '%'))")
    Page<ContentSummary> searchByWriter(@Param("word") String word, Pageable pageable);

    @Query(SUMMARY + "where lower(c.content) like lower(concat('%', :word, '%'))")
    Page<ContentSummary> searchByContent(@Param("word") String word, Pageable pageable);

    @Query("select c.title from Content c where c.id = :id")
    String findTitleById(@Param("id") Long id);

    /** 삭제된 글을 건너뛰고 실제로 존재하는 다음 글(id 가 큰 쪽) 하나. */
    Optional<Content> findFirstByIdGreaterThanOrderByIdAsc(Long id);

    /** 삭제된 글을 건너뛰고 실제로 존재하는 이전 글(id 가 작은 쪽) 하나. */
    Optional<Content> findFirstByIdLessThanOrderByIdDesc(Long id);
}
