package me.exrates.dao.impl;

import me.exrates.dao.NewsDao;
import me.exrates.model.dto.NewsSummaryDto;
import me.exrates.model.main.News;
import me.exrates.model.onlineTableDto.NewsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class NewsDaoImpl implements NewsDao {

    @Autowired
//    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    @Transactional
    public News getNews(final Integer newsId, Locale locale) {
        String sql = "SELECT id, title, date, brief, resource, news_variant" +
                " FROM NEWS" +
                " JOIN NEWS_VARIANTS ON (NEWS_VARIANTS.news_id = NEWS.id) " +
                " AND (NEWS_VARIANTS.news_variant = :news_variant)" +
                " AND (NEWS_VARIANTS.active = 1)" +
                " WHERE id = :news_id";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("news_id", newsId);
            put("news_variant", locale.toString().toUpperCase());
        }};
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, new RowMapper<News>() {
                @Override
                public News mapRow(ResultSet rs, int rowNum) throws SQLException {
                    News result = new News();
                    result.setId(rs.getInt("id"));
                    result.setTitle(rs.getString("title"));
                    result.setDate(rs.getTimestamp("date").toLocalDateTime().toLocalDate());
                    result.setBrief(rs.getString("brief"));
                    result.setResource(rs.getString("resource"));
                    result.setNewsVariant(rs.getString("news_variant"));
                    return result;
                }
            });
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public int addNews(News news) {
        String sql = "INSERT INTO NEWS" +
                " (date, resource)" +
                " VALUES" +
                " (:date, :resource)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("date", news.getDate());
            put("resource", news.getResource());
        }};
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        final int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            return 0;
        }
        return id;
    }

    @Override
    @Transactional
    public int addNewsVariant(News news) {
        String sql = " INSERT INTO NEWS_VARIANTS " +
                " (news_id, title, news_variant, brief)" +
                " VALUES" +
                " (:news_id, :title, :news_variant, :brief)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("news_id", news.getId());
            put("title", news.getTitle());
            put("news_variant", news.getNewsVariant());
            put("brief", news.getBrief());
        }};
        try {
            return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
        } catch (DuplicateKeyException e) {
            //provide an opportunity to update the file
            return 0;
        }
    }

    @Override
    @Transactional
    public List<NewsDto> getNewsBriefList(final Integer offset, final Integer limit, Locale locale) {
        String sql = "SELECT id, title, brief, date, resource, news_variant " +
                " FROM NEWS" +
                " JOIN NEWS_VARIANTS ON (NEWS_VARIANTS.news_id = NEWS.id) " +
                " AND (NEWS_VARIANTS.news_variant = :news_variant)" +
                " AND (NEWS_VARIANTS.active = 1)" +
                " ORDER BY date DESC, added_date DESC " +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("news_variant", locale.toString().toUpperCase());
        }};
        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            NewsDto result = new NewsDto();
            result.setId(rs.getInt("id"));
            result.setTitle(rs.getString("title"));
            result.setBrief(rs.getString("brief"));
            result.setDate(rs.getTimestamp("date").toLocalDateTime().toLocalDate());
            result.setResource(rs.getString("resource"));
            result.setVariant(rs.getString("news_variant"));
            result.setRef(new StringBuilder("/news/")
                    .append(rs.getString("resource"))
                    .append(rs.getString("id"))
                    .append("/")
                    .append(locale.toString())
                    .append("/").toString());
            return result;
        });
    }

    @Override
    @Transactional
    public int deleteNewsVariant(News news) {
        String sql = " DELETE FROM NEWS_VARIANTS " +
                " WHERE (news_id = :news_id) and (news_variant = :news_variant)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("news_id", news.getId());
            put("news_variant", news.getNewsVariant());
        }};
        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    @Override
    @Transactional
    public int deleteNews(News news) {
        String sql = " DELETE FROM NEWS_VARIANTS " +
                " WHERE (news_id = :news_id)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("news_id", news.getId());
            put("news_variant", news.getNewsVariant());
        }};
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
        /**/
        sql = " DELETE FROM NEWS " +
                " WHERE (id = :news_id)";
        params = new HashMap<String, Object>() {{
            put("news_id", news.getId());
        }};
        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    @Override
    public List<NewsSummaryDto> findAllNewsVariants() {
        String sql = "SELECT id, date, resource, title, news_variant, brief, active FROM NEWS " +
                "INNER JOIN NEWS_VARIANTS ON NEWS_VARIANTS.news_id = NEWS.id " +
                "ORDER BY id DESC, news_variant ASC";
        return namedParameterJdbcTemplate.query(sql, (resultSet) -> {
            List<NewsSummaryDto> news = new ArrayList<>();
            int prevId = 0;
            while (resultSet.next()) {
                int currentId = resultSet.getInt("id");
                if (currentId != prevId) {
                    prevId = currentId;
                    NewsSummaryDto dto = new NewsSummaryDto();
                    dto.setId(currentId);
                    dto.setDate(resultSet.getTimestamp("date").toLocalDateTime().toLocalDate());
                    dto.setResource(resultSet.getString("resource"));
                    dto.setTitle(resultSet.getString("title"));
                    dto.setBrief(resultSet.getString("brief"));
                    dto.setIsActive(resultSet.getBoolean("active"));
                    dto.getVariants().add(resultSet.getString("news_variant"));
                    news.add(dto);
                } else {
                    news.get(news.size() - 1).getVariants().add(resultSet.getString("news_variant"));
                }
            }

            return news;
        });
    }
}
