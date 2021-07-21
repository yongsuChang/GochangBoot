package kr.co.gochang.controller;

import kr.co.gochang.interfaces.CrudInterface;
import kr.co.gochang.model.network.Header;
import kr.co.gochang.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Component
public abstract class CrudController<Request, Response, Entity> implements CrudInterface<Request, Response> {

    @Autowired(required = false)
    protected BaseService<Request, Response, Entity> baseService;

    @Override
    @PostMapping("")
    public Header<Response> create(@RequestBody Header<Request> request) {
        return baseService.create(request);
    }

    @Override
    @GetMapping("{id}")
    public Header<Response> read(@PathVariable Long id) {
        return baseService.read(id);
    }

    @Override
    @PutMapping("")
    public Header<Response> update(@RequestBody Header<Request> request) {
        return baseService.update(request);
    }

    @Override
    @DeleteMapping
    public Header delete(@PathVariable Long id) {
        return baseService.delete(id);
    }

    @GetMapping("")
    public Header<List<Response>> search(
            @PageableDefault(sort="id", direction= Sort.Direction.ASC, size=15)
                    Pageable pageable){
        log.info("{}", pageable);
        return baseService.search(pageable);
    }
}
