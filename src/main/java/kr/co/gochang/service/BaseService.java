package kr.co.gochang.service;

import kr.co.gochang.interfaces.CrudInterface;
import kr.co.gochang.model.network.Header;
import kr.co.gochang.model.network.Pagination;
import kr.co.gochang.model.network.response.ContentApiResponse;
import kr.co.gochang.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public abstract class BaseService<Request, Response, Entity> implements CrudInterface<Request, Response> {
    @Autowired(required = false)
    protected JpaRepository<Entity, Long> baseRepository;

    public abstract Response response(Entity entity);

    public Header<List<Response>> search(Pageable pageable){
        Page<Entity> entities = baseRepository.findAll(pageable);
        List<Response> responseList = getResponseList(entities);
        Pagination pagination = PaginationUtils.buildPaginationByPageElement(entities);

        return Header.OK(responseList, pagination);
    }

    public List<Response> getResponseList(Page<Entity> entities){
        return entities.stream()
                .map(this::response)
                .collect(Collectors.toList());
    }

}
