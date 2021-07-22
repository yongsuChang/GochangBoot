package kr.co.gochang.service;

import kr.co.gochang.model.entity.Content;
import kr.co.gochang.model.entity.ContentMigration;
import kr.co.gochang.model.network.Header;
import kr.co.gochang.model.network.request.ContentMigrationApiRequest;
import kr.co.gochang.model.network.response.ContentMigrationApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentMigrationApiService
        extends BaseService
        <ContentMigrationApiRequest, ContentMigrationApiResponse, ContentMigration>{

    @Override
    public ContentMigrationApiResponse response(ContentMigration contentMigration) {
        return ContentMigrationApiResponse.builder()
                .id(contentMigration.getId())
                .number(contentMigration.getNumber())
                .title(contentMigration.getTitle())
                .writeDate(contentMigration.getWriteDate())
                .writer(contentMigration.getWriter())
                .count(contentMigration.getCount())
                .recommend(contentMigration.getRecommend())
                .replyCount(contentMigration.getReplyCount())
                .content(contentMigration.getContent())
                .picture(contentMigration.getPicture())
                .build();
    }

    @Override
    public Header<ContentMigrationApiResponse> create(Header<ContentMigrationApiRequest> request) {
        ContentMigrationApiRequest contentMigrationRequest = request.getData();
        ContentMigration contentMigration = ContentMigration.builder()
                .id(contentMigrationRequest.getId())
                .number(contentMigrationRequest.getNumber())
                .title(contentMigrationRequest.getTitle())
                .writeDate(contentMigrationRequest.getWriteDate())
                .writer(contentMigrationRequest.getWriter())
                .count(contentMigrationRequest.getCount())
                .recommend(contentMigrationRequest.getRecommend())
                .replyCount(contentMigrationRequest.getReplyCount())
                .content(contentMigrationRequest.getContent())
                .picture(contentMigrationRequest.getPicture())
                .build();
        ContentMigration newContentMigration = baseRepository.save(contentMigration);
        return Header.OK(response(newContentMigration));
    }

    @Override
    public Header<ContentMigrationApiResponse> read(Long id) {
        return null;
    }

    @Override
    public Header<ContentMigrationApiResponse> update(Header<ContentMigrationApiRequest> request) {
        return null;
    }

    @Override
    public Header delete(Long id) {
        return null;
    }

    public List<ContentMigrationApiResponse> responseList(List<ContentMigration> contentMigrationList){
        List<ContentMigrationApiResponse> contentMigrationApiResponseList = null;
        for(ContentMigration contentMigration : contentMigrationList){
            contentMigrationApiResponseList.add(response(contentMigration));
        }
        assert contentMigrationApiResponseList != null;
        return contentMigrationApiResponseList;
    }

    public Header<List<ContentMigrationApiResponse>> migrateByContentList(
            List<Content> contentList) {
        List<ContentMigration> contentMigrationApiResponseList = null;
        for(Content content : contentList) {
            ContentMigration contentMigration = ContentMigration.builder()
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
//            ContentMigration newContentMigration = baseRepository.save(contentMigration);
//            contentMigrationApiResponseList.add(newContentMigration);
            contentMigrationApiResponseList.add(contentMigration);
        }
        assert contentMigrationApiResponseList != null;
        return Header.OK(responseList(contentMigrationApiResponseList));

    }
}
