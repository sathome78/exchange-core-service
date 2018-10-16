package me.exrates.model.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PaginationWrapper<E extends List> {
    private E data;
    private int totalCount;
    private int pageSize;
    private int pagesCount;

    public PaginationWrapper(E data, int totalCount, int pageSize) {
        this.data = data;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        if (pageSize == 0) {
            this.pagesCount = 1;
        } else {
            this.pagesCount = (totalCount % pageSize == 0) ? (totalCount / pageSize) : (totalCount / pageSize + 1);
        }
    }
}
