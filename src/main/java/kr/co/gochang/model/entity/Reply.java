package kr.co.gochang.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "gochangreplyboot")
@Table(name = "gochangreplyboot")
@Builder
@Accessors(chain = true)
public class Reply {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "writedate")
    private String writeDate;

    @Column(name = "writer")
    private String writer;

    @Column(name = "content")
    private String content;

    @Column(name = "isrereply")
    private String isReReply;

    @Column(name = "indexincontent")
    private String indexInContent;

    // 원래는 @ManyToOne 등으로 연결 되어야
    @Column(name = "contentid")
    private Long contentId;

}
