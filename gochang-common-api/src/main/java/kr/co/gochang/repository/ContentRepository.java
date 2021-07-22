package kr.co.gochang.repository;

import java.util.List;
import kr.co.gochang.model.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    Page<Content> findAllByTitleContaining(Pageable pageable, String title);
    Page<Content> findAllByContentContaining(Pageable pageable, String content);
    Page<Content> findAllByWriterContaining(Pageable pageable, String writer);
}
