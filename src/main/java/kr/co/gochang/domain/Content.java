package kr.co.gochang.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.SQLRestriction;

/**
 * 크롤링해 온 원본 게시글. 아카이브라 절대 바뀌지 않으므로 읽기 전용 엔티티다.
 * 컬럼명은 원본 MySQL 테이블과 동일하게 유지해서 이전 작업을 단순하게 한다.
 */
@Getter
@Immutable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "content")
@SQLRestriction("is_deleted = false")
public class Content {

    @Id
    private Long id;

    /** 원본 게시판의 글 번호. reply.contentid 는 id 가 아니라 이 값을 가리킨다. */
    @Column(name = "number")
    private Long number;

    private String title;

    @Column(name = "writedate")
    private String writeDate;

    private String writer;

    @Column(name = "count")
    private String count;

    private String recommend;

    @Column(name = "replycount")
    private String replyCount;

    private String content;

    private String picture;

    @Column(name = "is_deleted")
    private boolean deleted;
}
