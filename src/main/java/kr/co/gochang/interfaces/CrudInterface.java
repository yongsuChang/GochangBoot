package kr.co.gochang.interfaces;

import kr.co.gochang.model.network.Header;

public interface CrudInterface<Request, Response> {

    Header<Response> create(Header<Request> request);

    Header<Response> read(Long id);

    Header<Response> update(Header<Request> request);

    Header delete(Long id);

}
