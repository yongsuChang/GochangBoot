package kr.co.gochang.api.dto;

import org.springframework.data.domain.Page;

public record PageInfo(int totalPages, long totalElements, int currentPage, int currentElements) {
    public static PageInfo of(Page<?> page) {
        return new PageInfo(page.getTotalPages(), page.getTotalElements(), page.getNumber(), page.getNumberOfElements());
    }
}
