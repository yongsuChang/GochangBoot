package kr.co.gochang.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Getter
@Immutable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reply")
public class Reply {

    @Id
    private Long id;

    @Column(name = "writedate")
    private String writeDate;

    private String writer;

    private String content;

    /** 원본 값 그대로("1" 이면 대댓글). 프론트가 문자열 비교를 하므로 타입을 바꾸지 않는다. */
    @Column(name = "isrereply")
    private String isReReply;

    /** 글 안에서의 댓글 순번. 정렬에 쓰이므로 숫자 컬럼이어야 한다. */
    @Column(name = "indexincontent")
    private Integer indexInContent;

    /** content.number 를 가리킨다 (content.id 가 아님). */
    @Column(name = "contentid")
    private Long contentNumber;
}
