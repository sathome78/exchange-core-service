package me.exrates.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ReferralLevel {

    private int id;
    private int level;
    private BigDecimal percent;

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ReferralLevel that = (ReferralLevel) o;

        if (id != that.id) return false;
        if (level != that.level) return false;
        return percent != null ? percent.equals(that.percent) : that.percent == null;

    }

    public int hashCode() {
        int result = id;
        result = 31 * result + level;
        result = 31 * result + (percent != null ? percent.hashCode() : 0);
        return result;
    }
}
