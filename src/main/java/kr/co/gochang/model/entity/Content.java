package kr.co.gochang.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "content")
@Table(name = "content")
@Builder
@Accessors(chain = true)
@Where(clause = "is_deleted = false")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "title")
    private String title;

    @Column(name = "writedate")
    private String writeDate;

    @Column(name = "writer")
    private String writer;

    @Column(name = "count")
    private String count;

    @Column(name = "recommend")
    private String recommend;

    @Column(name = "replycount")
    private String replyCount;

    @Column(name = "content")
    private String content;

    @Column(name = "picture")
    private String picture;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted;
}
