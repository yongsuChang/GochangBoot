package kr.co.gochang.service.api;

import java.util.List;
import kr.co.gochang.model.entity.Content;
import kr.co.gochang.model.entity.Reply;
import kr.co.gochang.model.enumclass.SearchType;
import kr.co.gochang.model.network.Header;
import kr.co.gochang.model.network.request.ContentApiRequest;
import kr.co.gochang.model.network.response.ContentApiResponse;
import kr.co.gochang.model.network.response.ReplyApiResponse;
import kr.co.gochang.repository.ContentRepository;
import kr.co.gochang.repository.ReplyRepository;
import kr.co.gochang.service.BaseService;
import kr.co.gochang.utils.PaginationUtils;
import kr.co.gochang.utils.SearchTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContentApiLogicService extends BaseService<ContentApiRequest, ContentApiResponse, Content> {

    @Autowired
    private ReplyRepository replyRepository;

    @Override
    public ContentApiResponse response(Content content) {
        return ContentApiResponse.builder()
                .id(content.getId())
                .number(content.getNumber())
                .title(content.getTitle())
                .writeDate(content.getWriteDate())
                .writer(content.getWriter())
                .count(content.getCount())
                .recommend(content.getRecommend())
                .replyCount(content.getReplyCount())
                .content(content.getContent())
                .picture(content.getPicture())
                .build();
    }

    @Override
    public Header<ContentApiResponse> create(Header<ContentApiRequest> request) {
        return null;
    }

    @Override
    public Header<ContentApiResponse> read(Long id) {
        return baseRepository.findById(id)
                .map(this::response)
                .map(Header::OK)
                .orElseGet(() -> Header.ERROR("해당 아이디로 검색된 게시글 없음"));
    }

    @Override
    public Header<ContentApiResponse> update(Header<ContentApiRequest> request) {
        return null;
    }

    @Override
    public Header delete(Long id) {
        return null;
    }

    // TODO: 지저분, 나중에 리팩토링 가능하면 해야
    public Header<List<ContentApiResponse>> searchBoard(
            Pageable pageable, String searchTypeString, String searchWord){
        ContentRepository contentRepository = (ContentRepository)baseRepository;

        // searchTypeString 검사
        SearchType searchTypeEnum = SearchTypeUtils.getSearchTypeByString(searchTypeString);
        if(searchTypeEnum == SearchType.NONE){
            return Header.ERROR("잘못된 검색 타입입니다");
        }

        // searchWord 검사 - 없으면 전체 검색 결과
        Page<Content> contentPage = null;
        if(searchWord.equals("")) {
            contentPage = contentRepository.findAll(pageable);
            return PaginationUtils.getPaginationHeader(contentPage, this);
        }

        switch (searchTypeEnum){
            case TITLE: {
                contentPage = contentRepository.findAllByTitleContaining(pageable, searchWord);
                break;
            }
            case CONTENT: {
                contentPage = contentRepository.findAllByContentContaining(pageable, searchWord);
                break;
            }
            case WRITER: {
                contentPage = contentRepository.findAllByWriterContaining(pageable, searchWord);
                break;
            }
            default: {
                contentPage = contentRepository.findAllByTitleContaining(pageable, searchWord);
            }
        }
        return PaginationUtils.getPaginationHeader(contentPage, this);
    }
}
