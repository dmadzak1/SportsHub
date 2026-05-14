package com.sportshub.facility.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResponseDTO<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageResponseDTO(List<T> content, int page, int size,
                           long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}