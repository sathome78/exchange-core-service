package me.exrates.model.main;

import java.util.Collection;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class PagingData<E extends Collection<?>> {

    private int total;
    private int filtered;
    private E data;

    public int getTotal() {
        return total;
    }

    public void setTotal(final int total) {
        this.total = total;
    }

    public int getFiltered() {
        return filtered;
    }

    public void setFiltered(final int filtered) {
        this.filtered = filtered;
    }

    public E getData() {
        return data;
    }

    public void setData(final E data) {
        this.data = data;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PagingData<?> that = (PagingData<?>) o;

        if (total != that.total) return false;
        if (filtered != that.filtered) return false;
        return data != null ? data.equals(that.data) : that.data == null;

    }

    @Override
    public int hashCode() {
        int result = total;
        result = 31 * result + filtered;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PagingData{" +
                "total=" + total +
                ", filtered=" + filtered +
                ", data=" + data +
                '}';
    }
}
