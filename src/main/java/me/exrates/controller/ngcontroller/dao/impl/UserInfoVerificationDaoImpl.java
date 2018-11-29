package me.exrates.controller.ngcontroller.dao.impl;

import me.exrates.controller.ngcontroller.dao.UserInfoVerificationDao;
import me.exrates.controller.ngcontroller.model.UserInfoVerificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.time.LocalDate;

@Repository
public class UserInfoVerificationDaoImpl implements UserInfoVerificationDao {

    private static final String INFO_TABLE_NAME = "USER_VERIFICATION_INFO";
    private static final String USER_ID_COL = "user_id";
    private static final String FIRST_NAME_COL = "first_name";
    private static final String LAST_NAME_COL = "last_name";
    private static final String BORN_COL = "born";
    private static final String RES_ADDR_COL = "residential_address";
    private static final String POST_CODE_COL = "postal_code";
    private static final String COUNTRY_COL = "country";
    private static final String CITY_COL = "city";


    private static final String USER_ID_KEY = "userId";

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    @Override
    public UserInfoVerificationDto save(UserInfoVerificationDto verificationDto) {
        String sql = "INSERT INTO " + INFO_TABLE_NAME + " (" + getInfoInsertColumns() + ")"
                + " VALUES (:userId, :firstName, :lastName, :born, :resAddr, :postCode, :country, :city)"
                + " ON DUPLICATE KEY UPDATE " + getInfoUpdateColumns();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, verificationDto.getUserId())
                .addValue("firstName", verificationDto.getFirstName())
                .addValue("lastName", verificationDto.getLastName())
                .addValue("born", getDBDate(verificationDto.getBorn()), Types.DATE)
                .addValue("resAddr", verificationDto.getResidentialAddress())
                .addValue("postCode", verificationDto.getPostalCode())
                .addValue("country", verificationDto.getCountry())
                .addValue("city", verificationDto.getCity());
        int rowsUpdated = namedParameterJdbcTemplate.update(sql, parameters);
        return rowsUpdated > 0 ? verificationDto : null;
    }

    @Override
    public boolean delete(UserInfoVerificationDto verificationDto) {
        String sql = "DELETE FROM " + INFO_TABLE_NAME + " WHERE  " + USER_ID_COL + " =:userId";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, verificationDto.getUserId());
        return namedParameterJdbcTemplate.update(sql, parameters) > 0;
    }

    @Override
    public UserInfoVerificationDto findByUserId(Integer userId) {
        UserInfoVerificationDto found = findById(userId);
        return null;
    }

    private UserInfoVerificationDto findById(Integer userId) {
        String sql = "SELECT * FROM " + INFO_TABLE_NAME + " WHERE " + USER_ID_COL + "=:userId";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, userId);
        try {
            return slaveJdbcTemplate.queryForObject(sql, parameterSource, getInfoRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private String getInfoInsertColumns() {
        return String.join(", ", USER_ID_COL, FIRST_NAME_COL, LAST_NAME_COL, BORN_COL,
                RES_ADDR_COL, POST_CODE_COL, COUNTRY_COL, CITY_COL);
    }

    private String getInfoUpdateColumns() {
        return FIRST_NAME_COL + "= :firstName, " + LAST_NAME_COL + "= :lastName, "
                + BORN_COL + "=:born, " + RES_ADDR_COL + "=:resAddr, " + POST_CODE_COL + "=:postCode, "
                + COUNTRY_COL + "=:country, " + CITY_COL + "=:city";
    }

    private java.sql.Date getDBDate(LocalDate when) {
        if (when == null ) {
            return null;
        }
        return java.sql.Date.valueOf(when);
    }

    private RowMapper<UserInfoVerificationDto> getInfoRowMapper() {
        return (rs, rowNum) ->
                UserInfoVerificationDto
                        .builder()
                        .userId(rs.getInt(USER_ID_COL))
                        .firstName(rs.getString(FIRST_NAME_COL))
                        .lastName(rs.getString(LAST_NAME_COL))
                        .born(rs.getDate(BORN_COL).toLocalDate())
                        .residentialAddress(rs.getString(RES_ADDR_COL))
                        .postalCode(rs.getString(POST_CODE_COL))
                        .country(rs.getString(COUNTRY_COL))
                        .city(rs.getString(CITY_COL))
                        .build();
    }
}
