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
@Entity(name = "gochangcontent")
@Table(name = "gochangcontent")
@Builder
@Accessors(chain = true)
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GOCHANGCONTENT_SEQ_GENERATOR")
    @SequenceGenerator(sequenceName = "GOCHANGCONTENT_SEQ", name="GOCHANGCONTENT_SEQ_GENERATOR", allocationSize = 1)
    @Column(name = "contentindex")
    private Long id;

    @Column(name = "contentnumber")
    private String number;

    @Column(name = "contenttitle")
    private String title;

    @Column(name = "contentwritedate")
    private String writeDate;

    @Column(name = "contentwriter")
    private String writer;

    @Column(name = "contentviewcount")
    private String count;

    @Column(name = "contentrecommend")
    private String recommend;

    @Column(name = "contentreplynumber")
    private String replyCount;

    @Column(name = "contentcontent")
    private String content;

    @Column(name = "contentpicture")
    private String picture;

}
