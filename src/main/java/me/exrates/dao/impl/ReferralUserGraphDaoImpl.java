package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ReferralUserGraphDao;
import me.exrates.model.dto.RefFilterData;
import me.exrates.model.dto.ReferralInfoDto;
import me.exrates.model.dto.ReferralProfitDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Log4j2
@Repository
public class ReferralUserGraphDaoImpl implements ReferralUserGraphDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    @Autowired
    //TODO @Qualifier(value = "masterTemplate")
    public ReferralUserGraphDaoImpl( final NamedParameterJdbcTemplate jdbcTemplate, @Qualifier(value = "namedParameterJdbcTemplate") NamedParameterJdbcTemplate slaveJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }


    @Override
    public void create(final int child, final int parent) {
        final String sql = "INSERT INTO REFERRAL_USER_GRAPH (child, parent) VALUES (:child, :parent)";
        final Map<String, Integer> params = new HashMap<>();
        params.put("child", child);
        params.put("parent", parent);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public Integer getParent(final Integer child) {
        final String sql = "SELECT parent FROM REFERRAL_USER_GRAPH WHERE child = :child";
        try {
            return jdbcTemplate.queryForObject(sql, singletonMap("child", child), Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Override
    public void changeReferralParent(Integer formerParent, Integer newParent) {
        String sql = "UPDATE REFERRAL_USER_GRAPH SET parent = :new_parent WHERE parent = :former_parent";
        Map<String, Integer> params = new HashMap<>();
        params.put("former_parent", formerParent);
        params.put("new_parent", newParent);
        jdbcTemplate.update(sql, params);
    }

    private RowMapper<ReferralInfoDto> getReferralInfoDtoRwoMapper() {
        return (rs, i) -> {
            ReferralInfoDto infoDto = new ReferralInfoDto();
            infoDto.setRefId(rs.getInt("ref_id"));
            infoDto.setEmail(rs.getString("email"));
            infoDto.setFirstRefLevelCount(rs.getInt("childs_count"));
            infoDto.setRefProfitFromUser(rs.getDouble("ref_profit"));
            return infoDto;
        };
    }


    @Override
    public List<ReferralInfoDto> getInfoAboutFirstLevRefs(int userId, int profitUser,
                                                          int limit, int offset, RefFilterData refFilterData) {
        String sql = "SELECT US.email AS email, US.id AS ref_id, " +
                "(SELECT COUNT(child) FROM REFERRAL_USER_GRAPH WHERE parent = RUG.child) AS childs_count, " +
                "sum(TR.amount) AS ref_profit " +
                "FROM REFERRAL_USER_GRAPH RUG " +
                "INNER JOIN USER US ON US.id = RUG.child " +
                "LEFT JOIN REFERRAL_TRANSACTION RT ON RT.initiator_id = US.id AND RT.user_id = RUG.parent and RT.status = 'PAYED' " +
                "LEFT JOIN TRANSACTION TR ON TR.source_type = 'REFERRAL' AND TR.source_id = RT.id %s " +
                "WHERE RUG.parent = :parent GROUP BY email";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("parent", userId);
        namedParameters.put("profit_user", profitUser);
        namedParameters.put("limit", limit);
        namedParameters.put("offset", offset);
        namedParameters.putAll((Map<String, Object>) refFilterData.getSQLParamsMap().get("params"));
        sql = String.format(sql, (String) refFilterData.getSQLParamsMap().get("sql"));
        if (offset >= 0 && limit > 0) {
            sql = sql.concat(" LIMIT :limit OFFSET :offset ");
        }
        try {
            return slaveJdbcTemplate.query(sql, namedParameters, getReferralInfoDtoRwoMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public ReferralInfoDto getInfoAboutUserRef(int userId, int profitUser, RefFilterData refFilterData) {
        String sql = "SELECT US.email AS email, US.id AS ref_id, " +
                "(SELECT COUNT(child) FROM REFERRAL_USER_GRAPH WHERE parent = :parent) AS childs_count, " +
                "sum(TR.amount) AS ref_profit " +
                "FROM USER US " +
                "LEFT JOIN REFERRAL_TRANSACTION RT ON RT.initiator_id = US.id AND RT.user_id = :profit_user AND RT.status = 'PAYED' " +
                "LEFT JOIN TRANSACTION TR ON TR.source_type = 'REFERRAL' AND TR.source_id = RT.id %s " +
                "WHERE US.id = :parent ";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("parent", userId);
        namedParameters.put("profit_user", profitUser);
        namedParameters.putAll((Map<String, Object>) refFilterData.getSQLParamsMap().get("params"));
        sql = String.format(sql, (String) refFilterData.getSQLParamsMap().get("sql"));
        try {
            return slaveJdbcTemplate.queryForObject(sql, namedParameters, getReferralInfoDtoRwoMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<ReferralProfitDto> detailedCountRefsTransactions(Integer userId, int profitUser, RefFilterData refFilterData) {
        String sql = "SELECT sum(TR.amount) AS ref_profit, CU.name AS currency_name FROM USER US " +
                "LEFT JOIN REFERRAL_TRANSACTION RT ON RT.initiator_id = US.id AND RT.user_id = :profit_user AND RT.status = 'PAYED' " +
                "LEFT JOIN TRANSACTION TR ON TR.source_type = 'REFERRAL' AND TR.source_id = RT.id %s " +
                "INNER JOIN CURRENCY CU ON CU.id = TR.currency_id ";
        if (userId != null) {
            sql = sql.concat(" WHERE US.id = :userId ");
        }
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("userId", userId);
        namedParameters.put("profit_user", profitUser);
        namedParameters.putAll((Map<String, Object>) refFilterData.getSQLParamsMap().get("params"));
        sql = String.format(sql, (String) refFilterData.getSQLParamsMap().get("sql"));
        sql = sql.concat(" GROUP BY CU.id ");
        try {
            return slaveJdbcTemplate.query(sql, namedParameters, new RowMapper<ReferralProfitDto>() {
                @Override
                public ReferralProfitDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ReferralProfitDto profitDto = new ReferralProfitDto();
                    profitDto.setAmount(rs.getBigDecimal("ref_profit").toPlainString());
                    profitDto.setCurrencyName(rs.getString("currency_name"));
                    return profitDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int getInfoAboutFirstLevRefsTotalSize(int parentId) {
        String sql = "SELECT COUNT(child) FROM REFERRAL_USER_GRAPH " +
                "WHERE parent = :parent";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("parent", parentId);
        try {
            return slaveJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

}
