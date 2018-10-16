package me.exrates.model.dto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by OLEG on 28.02.2017.
 */
public abstract class TableFilterData {
    private List<FilterDataItem> filterItems;

    public abstract void initFilterItems();

    void populateFilterItemsNonEmpty(FilterDataItem[] items) {
        filterItems = Stream.of(items).filter(checkNotEmpty())
                .collect(Collectors.toList());
    }

    private Predicate<FilterDataItem> checkNotEmpty() {
        return item -> !(item.getValue() == null
                || String.valueOf(item.getValue()).isEmpty()
                || (item.getValue() instanceof Collection && ((Collection) item.getValue()).isEmpty()));
    }

    public Map<String, Object> getNamedParams() {
        return filterItems.stream().collect(Collectors.toMap(FilterDataItem::getName, FilterDataItem::getValue));
    }

    public String getSQLFilterClause() {
        return filterItems.stream().map(item -> item.getSqlClause().concat(" ").concat(item.formatParamForSql()))
                .collect(Collectors.joining(" AND "));
    }

}
