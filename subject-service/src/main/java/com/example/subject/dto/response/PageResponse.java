package com.example.subject.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {}

