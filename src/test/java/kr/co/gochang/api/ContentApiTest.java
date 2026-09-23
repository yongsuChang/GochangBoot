package kr.co.gochang.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ContentApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void list_excludesDeleted_sortsByIdDesc_andOmitsBody() throws Exception {
        mvc.perform(get("/api/contents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("OK"))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].id").value(4))
                .andExpect(jsonPath("$.data[0].write_date").value("2015-03-04 13:00"))
                .andExpect(jsonPath("$.data[0].reply_count").value("1"))
                .andExpect(jsonPath("$.data[0].content").doesNotExist())
                .andExpect(jsonPath("$.pagination.total_elements").value(3))
                .andExpect(jsonPath("$.pagination.total_pages").value(1))
                .andExpect(jsonPath("$.pagination.current_page").value(0))
                .andExpect(jsonPath("$.pagination.current_elements").value(3));
    }

    @Test
    void list_respectsPageAndSizeAndCapsSize() throws Exception {
        mvc.perform(get("/api/contents").param("page", "1").param("size", "2"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.pagination.total_pages").value(2));

        mvc.perform(get("/api/contents").param("size", "100000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    void search_byTitle_isCaseInsensitive() throws Exception {
        mvc.perform(get("/api/contents/search").param("searchType", "title").param("searchWord", "사진"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(2));

        mvc.perform(get("/api/contents/search").param("searchType", "content").param("searchWord", "gochang"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(2));

        mvc.perform(get("/api/contents/search").param("searchType", "writer").param("searchWord", "홍길동"))
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    void search_emptyWord_returnsAll_andBadTypeIsError() throws Exception {
        mvc.perform(get("/api/contents/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)));

        mvc.perform(get("/api/contents/search").param("searchType", "bogus").param("searchWord", "x"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("ERROR"));
    }

    @Test
    void detail_and_title() throws Exception {
        mvc.perform(get("/api/contents/2"))
                .andExpect(jsonPath("$.data.title").value("두 번째 후기 사진 첨부"))
                .andExpect(jsonPath("$.data.content").value("<p>두 번째 본문. Gochang Nongak</p>"))
                .andExpect(jsonPath("$.data.picture").value("1"));

        mvc.perform(get("/api/contents/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("ERROR"));

        mvc.perform(get("/api/contents/2/title"))
                .andExpect(jsonPath("$.data").value("두 번째 후기 사진 첨부"));
        mvc.perform(get("/api/contents/999/title"))
                .andExpect(jsonPath("$.data").value("게시물 없음"));
    }

    @Test
    void neighbors_skipDeletedContent() throws Exception {
        // 3번은 삭제된 글이므로 2번의 prev 는 4번, 4번의 next 는 2번
        mvc.perform(get("/api/contents/2/neighbors"))
                .andExpect(jsonPath("$.data.prev.id").value(4))
                .andExpect(jsonPath("$.data.prev.title").value("세 번째 후기"))
                .andExpect(jsonPath("$.data.next.id").value(1));
        mvc.perform(get("/api/contents/4/neighbors"))
                .andExpect(jsonPath("$.data.prev").doesNotExist())
                .andExpect(jsonPath("$.data.next.id").value(2));
        mvc.perform(get("/api/contents/1/neighbors"))
                .andExpect(jsonPath("$.data.next").doesNotExist());
    }

    @Test
    void replies_areLookedUpByContentNumber_andSortedNumerically() throws Exception {
        mvc.perform(get("/api/replies/byContent/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(12)))
                .andExpect(jsonPath("$.data[0].index_in_content").value(1))
                .andExpect(jsonPath("$.data[9].index_in_content").value(10))
                .andExpect(jsonPath("$.data[11].index_in_content").value(12))
                .andExpect(jsonPath("$.data[2].is_re_reply").value("1"))
                .andExpect(jsonPath("$.data[0].content_id").value(1001))
                .andExpect(jsonPath("$.pagination.total_elements").value(12));

        mvc.perform(get("/api/replies/byContent/4"))
                .andExpect(jsonPath("$.data", hasSize(1)));
        mvc.perform(get("/api/replies/byContent/999"))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void badSortProperty_isJson400_andUnknownPathIsJson404() throws Exception {
        mvc.perform(get("/api/contents").param("sort", "nope,desc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result_code").value("ERROR"));

        mvc.perform(get("/api/nothing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result_code").value("ERROR"));
    }
}
