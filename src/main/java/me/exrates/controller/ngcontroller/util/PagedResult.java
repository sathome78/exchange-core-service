package me.exrates.controller.ngcontroller.util;

import java.util.ArrayList;
import java.util.List;

public class PagedResult<T> {

    private int count;
    private List<T> items;

    public PagedResult() {
        items = new ArrayList<>();
    }

    public PagedResult(int count, List<T> items) {
        this.count = count;
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
