package me.exrates.model.main;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class DataTable<E> {

    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private E data;
    private String error;

    public int getDraw() {
        return draw;
    }

    public void setDraw(final int draw) {
        this.draw = draw;
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(final int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public int getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(final int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public E getData() {
        return data;
    }

    public void setData(final E data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DataTable<?> that = (DataTable<?>) o;

        if (draw != that.draw) return false;
        if (recordsTotal != that.recordsTotal) return false;
        if (recordsFiltered != that.recordsFiltered) return false;
        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        return error != null ? error.equals(that.error) : that.error == null;

    }

    @Override
    public int hashCode() {
        int result = draw;
        result = 31 * result + recordsTotal;
        result = 31 * result + recordsFiltered;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataTable{" +
                "draw=" + draw +
                ", recordsTotal=" + recordsTotal +
                ", recordsFiltered=" + recordsFiltered +
                ", data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}
