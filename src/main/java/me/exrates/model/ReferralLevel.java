package me.exrates.model;

import java.math.BigDecimal;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class ReferralLevel {

    private int id;
    private int level;
    private BigDecimal percent;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(final BigDecimal percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "ReferralLevel{" +
                "id=" + id +
                ", level=" + level +
                ", percent=" + percent +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ReferralLevel that = (ReferralLevel) o;

        if (id != that.id) return false;
        if (level != that.level) return false;
        return percent != null ? percent.equals(that.percent) : that.percent == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + level;
        result = 31 * result + (percent != null ? percent.hashCode() : 0);
        return result;
    }
}
