package kr.co.gochang.controller;

import kr.co.gochang.controller.api.ContentApiController;
import kr.co.gochang.model.entity.Content;
import kr.co.gochang.model.entity.ContentMigration;
import kr.co.gochang.model.network.Header;
import kr.co.gochang.model.network.request.ContentMigrationApiRequest;
import kr.co.gochang.model.network.response.ContentMigrationApiResponse;
import kr.co.gochang.service.ContentMigrationApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/migrate")
public class ContentMigrationController extends CrudController<ContentMigrationApiRequest, ContentMigrationApiResponse, ContentMigration> {

    @Autowired
    private ContentApiController contentApiController;

    @GetMapping("/letgo")
    public Header<List<ContentMigrationApiResponse>> migrateByContentApiResponse(){
        ContentMigrationApiService contentMigrationService = (ContentMigrationApiService)baseService;
        List<Content> contentList = contentApiController.readAll();
        return contentMigrationService.migrateByContentList(contentList);
    }
}
