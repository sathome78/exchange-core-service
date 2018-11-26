package me.exrates.dao.impl;

import me.exrates.dao.PageLayoutSettingsDao;
import me.exrates.model.dto.PageLayoutSettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Optional;

@Repository
public class PageLayoutSettingsDaoImpl implements PageLayoutSettingsDao {

    private static final String TABLE_NAME = "USER_PAGE_LAYOUT_SETTINGS";
    private static String USER_ID_COL = "user_id";
    private static String COLOR_SCHEME_COL = "color_scheme";
    private static String IS_LOW_COLOR_COL = "is_low_color_enabled";

    private static final String USER_ID_KEY = "userId";


    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    @Override
    public PageLayoutSettingsDto save(PageLayoutSettingsDto settingsDto) {
        String sql = "INSERT INTO " + TABLE_NAME
                + " (" + String.join(", ", USER_ID_COL, COLOR_SCHEME_COL, IS_LOW_COLOR_COL) + ")"
                + " VALUES (:userId, :colorScheme, :isLowColor)"
                + " ON DUPLICATE KEY UPDATE " + COLOR_SCHEME_COL + "= :colorScheme, " + IS_LOW_COLOR_COL + "= :isLowColor";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, settingsDto.getUserId())
                .addValue("colorScheme", settingsDto.getScheme(), Types.VARCHAR)
                .addValue("isLowColor", settingsDto.isLowColorEnabled());
        int rowsUpdated = namedParameterJdbcTemplate.update(sql, parameters);
        return rowsUpdated > 0  ? settingsDto : null;
    }

    @Override
    public Optional<PageLayoutSettingsDto> findByUserId(Integer userId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE  " + USER_ID_COL + " =:userId";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, userId);
        return slaveJdbcTemplate.query(sql, parameters, getOptionalExtractor());
    }

    @Override
    public boolean delete(PageLayoutSettingsDto settingsDto) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE  " + USER_ID_COL + " =:userId";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, settingsDto.getUserId());

        return namedParameterJdbcTemplate.update(sql, parameters) > 0;
    }

    private RowMapper<PageLayoutSettingsDto> getRowMapper() {
        return (rs, rowNum) -> PageLayoutSettingsDto
                .builder()
                .userId(rs.getInt(USER_ID_COL))
                .scheme(ColorScheme.of(rs.getString(COLOR_SCHEME_COL)))
                .isLowColorEnabled(rs.getBoolean(IS_LOW_COLOR_COL))
                .build();
    }

    private ResultSetExtractor<Optional<PageLayoutSettingsDto>> getOptionalExtractor() {
        return rs -> rs.next() ? Optional.of(getRowMapper().mapRow(rs, 1)) : Optional.empty();
    }
}
