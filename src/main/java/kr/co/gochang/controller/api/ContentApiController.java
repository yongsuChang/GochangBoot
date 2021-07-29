package kr.co.gochang.controller.api;

import kr.co.gochang.controller.CrudController;
import kr.co.gochang.model.entity.Content;
import kr.co.gochang.model.network.Header;
import kr.co.gochang.model.network.request.ContentApiRequest;
import kr.co.gochang.model.network.response.ContentApiResponse;
import kr.co.gochang.service.api.ContentApiLogicService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
public class ContentApiController extends CrudController<ContentApiRequest, ContentApiResponse, Content> {

    @GetMapping("/search")
    public Header<List<ContentApiResponse>> searchBoard(
            @PageableDefault(sort="id", direction= Sort.Direction.DESC, size=15)
                    Pageable pageable,
            // TODO: searchType으로 바꿀 것
            @RequestParam(value = "searchType", defaultValue = "") String searchType,
            @RequestParam(value = "searchWord", defaultValue = "") String searchWord) {
        ContentApiLogicService contentApiLogicService = (ContentApiLogicService) baseService;
        return contentApiLogicService.searchBoard(pageable, searchType, searchWord);
    }
}
