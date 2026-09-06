package kr.co.gochang.api;

import kr.co.gochang.api.dto.ApiResponse;
import kr.co.gochang.api.dto.ReplyResponse;
import kr.co.gochang.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @GetMapping("/byContent/{contentId}")
    public ApiResponse<List<ReplyResponse>> byContent(
            @PageableDefault(sort = "indexInContent", direction = Sort.Direction.ASC, size = 15) Pageable pageable,
            @PathVariable Long contentId) {
        return ApiResponse.ok(replyService.byContentId(contentId, pageable));
    }
}
