package kr.co.gochang.utils;

import kr.co.gochang.model.network.Header;
import kr.co.gochang.model.network.Pagination;
import kr.co.gochang.service.BaseService;
import org.springframework.data.domain.Page;

import java.util.List;

public final class PaginationUtils {

    /** Utility class는 인스턴스화 되면 안 됨 */
    private PaginationUtils() {};

    public static <ApiResponse> Header<List<ApiResponse>> getPaginationHeader(
            Page<?> pageElement, BaseService baseService){
        return Header.OK(
                baseService.getResponseList(pageElement),
                buildPaginationByPageElement(pageElement)
        );
    }

    public static Pagination buildPaginationByPageElement(Page<?> pageElement){

        return Pagination.builder()
                .totalPages(pageElement.getTotalPages())
                .totalElements(pageElement.getTotalElements())
                .currentPage(pageElement.getNumber())
                .currentElements(pageElement.getNumberOfElements())
                .build();
    }
}
