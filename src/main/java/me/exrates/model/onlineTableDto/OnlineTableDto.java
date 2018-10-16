package me.exrates.model.onlineTableDto;

public abstract class OnlineTableDto {
    public boolean needRefresh;
    protected int page;

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
