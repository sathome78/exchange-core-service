package me.exrates.dao.impl;

import me.exrates.dao.SurveyDao;
import me.exrates.model.dto.SurveyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyDaoImpl implements SurveyDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SurveyDto findFirstActiveByLang(String lang) {
        String sql = "SELECT S.*, SI.*, SLP.* " +
                "  FROM SURVEY S " +
                "  JOIN SURVEY_LANG_PARAM SLP ON (SLP.survey_id = S.id) AND (SLP.lang = :lang)" +
                "  JOIN SURVEY_ITEM SI ON (SI.survey_id = S.id) AND (SI.lang = :lang)" +
                "  WHERE S.id=(SELECT MIN(id) FROM SURVEY WHERE active = 1) ";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("lang", lang);
        final SurveyDto surveyDto = new SurveyDto();
        namedParameterJdbcTemplate.query(sql, param, (rs, i) -> {
            if (surveyDto.getId() == null) {
                surveyDto.setId(rs.getInt("id"));
                surveyDto.setToken(rs.getString("token"));
                surveyDto.setJson(rs.getString("json"));
                surveyDto.setDescription(rs.getString("description"));
            }
            surveyDto.getItems().add(new SurveyDto.SurveyItem(
                    rs.getString("name"),
                    rs.getString("title")));
            return null;
        });
        return surveyDto;
    }
}
