package me.exrates.model.dto;

import me.exrates.model.onlineTableDto.OnlineTableDto;

import java.util.List;

public class TableParams {
    private String tableId;
    private Integer pageSize;
    private Integer pageNumber;
    private boolean eof;
    private Integer offset;
    private Integer limit;

    /**/
    /*service methods*/
    public void setOffsetAndLimitForSql(Integer requestedPageNumber, PagingDirection requestedDirection) {
        requestedPageNumber = requestedPageNumber == null ?
                pageNumber == null ? 1 : pageNumber
                : requestedPageNumber;
        if (pageSize == -1) {
            /*ignore pageNumber and direction if the page has no size (unlimited)*/
            offset = 0;
            limit = -1;
        } else {
            if (requestedDirection != null) {
                /*the direction has priority over the pageNumber*/
                requestedPageNumber = pageNumber == null ? 1 : pageNumber;
                if (!eof || requestedDirection == PagingDirection.BACKWARD) {
                    requestedPageNumber += requestedDirection.getDirection();
                }
                requestedPageNumber = Math.max(requestedPageNumber, 1);
            }
            /**/
            offset = (requestedPageNumber - 1) * pageSize;
            limit = pageSize;
        }
        /*store resulted requestedPageNumber - it is current page now*/
        pageNumber = requestedPageNumber;
    }

    public void updateEofState(List<? extends OnlineTableDto> result) {
        if (pageSize != -1) {
            if (result.size() < pageSize) {
                if (result.isEmpty()) {
                    eof = true;
                } else {
                    if (result.get(0).isNeedRefresh()) {
                        eof = true;
                    }
                }
            } else {
                if (result.get(0).isNeedRefresh()) {
                    eof = false;
                }
            }
        }
    }

    /*getters setters*/

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public boolean isEof() {
        return eof;
    }

    public void setEof(boolean eof) {
        this.eof = eof;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
