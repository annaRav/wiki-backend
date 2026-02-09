package com.axis.goal.model.dto;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize,
    boolean first,
    boolean last
) {
    public static <T> PageResponse<T> of(List<T> content, long totalElements, int pageNumber, int pageSize) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return new PageResponse<>(
            content,
            totalElements,
            totalPages,
            pageNumber,
            pageSize,
            pageNumber == 0,
            pageNumber >= totalPages - 1
        );
    }
}
