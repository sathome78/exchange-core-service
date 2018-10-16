package me.exrates.model.dto;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RefFilterData {

    private String email;
    private String dateFrom;
    private String dateTo;
    private List<Integer> currencyIds;

    public Map<String, Object> getSQLParamsMap() {
        StringBuilder sqlWhere = new StringBuilder();
        Map<String, Object> paramsMap = new HashMap<>();
        if (!StringUtils.isEmpty(dateFrom)) {
            paramsMap.put("date_from", dateFrom);
            sqlWhere.append(" AND TR.datetime >= STR_TO_DATE(:date_from, '%Y-%m-%d %H:%i:%s') ");
        }
        if (!StringUtils.isEmpty(dateTo)) {
            paramsMap.put("date_to", dateTo);
            sqlWhere.append(" AND TR.datetime <= STR_TO_DATE(:date_to, '%Y-%m-%d %H:%i:%s') ");
        }
        if (currencyIds != null) {
            paramsMap.put("currencyIds", currencyIds);
            sqlWhere.append(" AND (TR.currency_id IN (:currencyIds)) ");
        }
        Map<String, Object> resultmap = new HashMap<>();
        resultmap.put("params", paramsMap);
        resultmap.put("sql", sqlWhere.toString());
        return resultmap;
    }
}
