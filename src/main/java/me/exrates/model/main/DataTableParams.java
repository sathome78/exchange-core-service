package me.exrates.model.main;

import lombok.extern.log4j.Log4j2;
import me.exrates.exception.IllegalColumnNameException;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 21.02.2017.
 */
@Log4j2
public class DataTableParams {

    private final String SEARCH_VALUE_KEY = "search_value";

    private int draw = 1;
    private int orderColumn = 0;
    private OrderDirection orderDirection = OrderDirection.ASC;
    private int start = 0;
    private int length = 0;
    private String searchValue = "";
    private List<String> columns = Collections.EMPTY_LIST;


    enum OrderDirection {
        ASC, DESC
    }

    private DataTableParams() {
    }

    public int getDraw() {
        return draw;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public List<String> getColumns() {
        return columns;
    }

    public static DataTableParams resolveParamsFromRequest(Map<String, String> requestParams) {
        DataTableParams dataTableParams = new DataTableParams();
        dataTableParams.draw = Integer.parseInt(requestParams.getOrDefault("draw", "1"));
        dataTableParams.orderColumn = Integer.parseInt(requestParams.getOrDefault("order[0][column]", "0"));
        dataTableParams.orderDirection = OrderDirection.valueOf(requestParams.getOrDefault("order[0][dir]", "asc").toUpperCase());
        dataTableParams.start = Integer.parseInt(requestParams.getOrDefault("start", "0"));
        dataTableParams.length = Integer.parseInt(requestParams.getOrDefault("length", "0"));
        dataTableParams.searchValue = requestParams.getOrDefault("search[value]", "");
        List<String> columnNames = requestParams.entrySet().stream()
                .filter(entry -> entry.getKey().matches("^columns(.+)name\\]$"))
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (columnNames.contains("WITHDRAW_REQUEST.id")) {
            columnNames = requestParams.entrySet().stream()
                    .filter(entry -> entry.getKey().matches("^columns(.+)name\\]$"))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }
        validateColumnNames(columnNames);
        dataTableParams.columns = columnNames;
        return dataTableParams;
    }

    public static DataTableParams defaultParams() {
        return new DataTableParams();
    }

    public static DataTableParams sortNoPaginationParams(String sortColumn, String sortDirection) {
        DataTableParams dataTableParams = new DataTableParams();
        dataTableParams.columns = Collections.singletonList(sortColumn);
        dataTableParams.orderDirection = OrderDirection.valueOf(sortDirection.toUpperCase());
        return dataTableParams;
    }

    public String getOrderByClause() {
        if (columns.isEmpty() || orderColumn >= columns.size()) {
            return "";
        }
        return new StringJoiner(" ").add("ORDER BY").add(columns.get(orderColumn)).add(orderDirection.name()).toString();
    }

    public String getSearchClause() {
        return StringUtils.isEmpty(searchValue) ? "" : columns.stream().map(columnName ->
                String.format("CONVERT(%s USING utf8) LIKE :%s", columnName, SEARCH_VALUE_KEY))
                .collect(Collectors.joining(" OR ", "(", ")"));
    }

    public String getSearchByEmailAndNickClause() {
        return StringUtils.isEmpty(searchValue) ? "" :
                String.format("( CONVERT(%s USING utf8) LIKE :%s ", "USER.email", SEARCH_VALUE_KEY)
                        .concat(" OR ")
                        .concat(String.format(" CONVERT(%s USING utf8) LIKE :%s )", "USER.nickname", SEARCH_VALUE_KEY));
    }

    public String getSearchByEmailAndNickClauseForVouchers() {
        return StringUtils.isEmpty(searchValue) ? "" :
                String.format("( CONVERT(%s USING utf8) LIKE :%s ", "UC.email", SEARCH_VALUE_KEY)
                        .concat(" OR ")
                        .concat(String.format(" CONVERT(%s USING utf8) LIKE :%s )", "UC.nickname", SEARCH_VALUE_KEY));
    }


    public Map<String, String> getSearchNamedParams() {
        return Collections.singletonMap(SEARCH_VALUE_KEY, String.format("%%%s%%", searchValue));
    }

    public String getLimitAndOffsetClause() {
        String limit;
        if (length > 0) {
            String offset = start > 0 ? " OFFSET :offset " : "";
            limit = " LIMIT :limit " + offset;
        } else {
            limit = "";
        }
        return limit;
    }

    private static void validateColumnNames(List<String> columnNames) {
        if (!columnNames.stream().filter(StringUtils::isNotEmpty)
                .allMatch(Pattern.compile("^[a-zA-z]+([_.]{1}[a-zA-z]+)*[a-zA-z]$").asPredicate())) {
            throw new IllegalColumnNameException("Illegal column name!");
        }
    }

    @Override
    public String toString() {
        return "DataTableParams{" +
                "draw=" + draw +
                ", orderColumn=" + orderColumn +
                ", orderDirection=" + orderDirection +
                ", start=" + start +
                ", length=" + length +
                ", searchValue='" + searchValue + '\'' +
                ", columns=" + columns +
                '}';
    }

}
