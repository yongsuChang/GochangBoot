package kr.co.gochang.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    /**
     * 게시글 id 로 댓글을 찾는다. reply 는 content.id 가 아니라 content.number 를 들고 있어서
     * 서브쿼리로 한 번에 해결한다 (이전에는 왕복 쿼리 2회).
     */
    @Query(value = "select r from Reply r "
            + "where r.contentNumber = (select c.number from Content c where c.id = :contentId)",
           countQuery = "select count(r) from Reply r "
            + "where r.contentNumber = (select c.number from Content c where c.id = :contentId)")
    Page<Reply> findByContentId(@Param("contentId") Long contentId, Pageable pageable);
}
