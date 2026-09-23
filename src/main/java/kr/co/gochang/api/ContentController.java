package kr.co.gochang.api;

import kr.co.gochang.api.dto.ApiResponse;
import kr.co.gochang.api.dto.ContentDetail;
import kr.co.gochang.api.dto.ContentSummary;
import kr.co.gochang.api.dto.Neighbors;
import kr.co.gochang.domain.SearchType;
import kr.co.gochang.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping
    public ApiResponse<List<ContentSummary>> list(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 15) Pageable pageable) {
        return ApiResponse.ok(contentService.list(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<List<ContentSummary>> search(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 15) Pageable pageable,
            @RequestParam(value = "searchType", defaultValue = "title") String searchType,
            @RequestParam(value = "searchWord", defaultValue = "") String searchWord) {
        return SearchType.fromParam(searchType)
                .map(type -> ApiResponse.ok(contentService.search(type, searchWord, pageable)))
                .orElseGet(() -> ApiResponse.error("잘못된 검색 타입입니다"));
    }

    @GetMapping("/{id}")
    public ApiResponse<ContentDetail> get(@PathVariable Long id) {
        return contentService.get(id)
                .map(ApiResponse::ok)
                .orElseGet(() -> ApiResponse.error("해당 아이디로 검색된 게시글 없음"));
    }

    @GetMapping("/{id}/neighbors")
    public ApiResponse<Neighbors> neighbors(@PathVariable Long id) {
        return ApiResponse.ok(contentService.neighbors(id));
    }

    @GetMapping("/{id}/title")
    public ApiResponse<String> title(@PathVariable Long id) {
        return ApiResponse.ok(contentService.title(id).orElse("게시물 없음"));
    }
}
