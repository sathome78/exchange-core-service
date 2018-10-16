package me.exrates.model.dto;

import lombok.*;

/**
 * Created by OLEG on 28.02.2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FilterDataItem {

    private static final String DEFAULT_FORMAT = ":%s";
    public static final String IN_FORMAT = "(:%s)";
    public static final String DATE_FORMAT = "STR_TO_DATE(:%s, '%%Y-%%m-%%d %%H:%%i:%%s')";
    public static final String LIKE_FORMAT_MIDDLE = "CONCAT('%%', :%s, '%%')";

    private String name;
    private String sqlClause;
    private Object value;
    private String format;

    public FilterDataItem(String name, String sqlClause, Object value) {
        this(name, sqlClause, value, DEFAULT_FORMAT);
    }

    public String formatParamForSql() {
        return String.format(format, name);
    }
}
