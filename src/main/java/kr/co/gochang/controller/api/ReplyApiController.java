package kr.co.gochang.controller.api;

import java.util.List;
import kr.co.gochang.controller.CrudController;
import kr.co.gochang.model.entity.Reply;
import kr.co.gochang.model.network.Header;
import kr.co.gochang.model.network.request.ReplyApiRequest;
import kr.co.gochang.model.network.response.ReplyApiResponse;
import kr.co.gochang.service.api.ReplyApiLogicService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReplyApiController extends CrudController<ReplyApiRequest, ReplyApiResponse, Reply> {

    @GetMapping("api/content/{contentId}/replies")
    public Header<List<ReplyApiResponse>> readReplyListByContentId(
            @PageableDefault(sort="id", direction= Sort.Direction.DESC, size=15)
                    Pageable pageable,
            @PathVariable(value = "contentId") Long contentId
    ){
        ReplyApiLogicService replyApiLogicService = (ReplyApiLogicService)baseService;
        return replyApiLogicService.readReplyListsByContentId(pageable, contentId);
    }
}
