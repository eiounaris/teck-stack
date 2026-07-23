package com.eiou.mall.common.api;

import java.util.List;

public record PageResponse<T>(
        long page,
        long size,
        long total,
        long pages,
        List<T> records
) {

    public static <T> PageResponse<T> of(long page, long size, long total, List<T> records) {
        long pages = size == 0 ? 0 : (total + size - 1) / size;
        return new PageResponse<>(page, size, total, pages, records);
    }
}
