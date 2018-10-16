package me.exrates.dao.impl;

import me.exrates.dao.ChatDao;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.main.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

import static java.util.Collections.singletonMap;

@Repository
public class ChatDaoImpl implements ChatDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ChatDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ChatMessage> findLastMessages(final ChatLang lang, final int messageCount) {
        final String sql = "SELECT c.id, c.user_id, c.body, c.message_time, USER.nickname FROM CHAT_" + lang.val +
                " as c INNER JOIN USER ON c.user_id = USER.id ORDER BY c.id DESC LIMIT :limit";
        return jdbcTemplate.query(sql, singletonMap("limit", messageCount), (resultSet, i) -> {
            final ChatMessage message = new ChatMessage();
            message.setId(resultSet.getLong("id"));
            message.setNickname(resultSet.getString("nickname"));
            message.setUserId(resultSet.getInt("user_id"));
            message.setBody(resultSet.getString("body"));
            message.setTime(resultSet.getTimestamp("message_time").toLocalDateTime());
            return message;
        });
    }


    @Override
    public void persist(final ChatLang lang, final NavigableSet<ChatMessage> message) {
        final String sql = "INSERT INTO CHAT_" + lang.val + "(id, user_id, body, message_time) VALUES (:id, :userId, :body, :time) " +
                " ON DUPLICATE KEY UPDATE body = :body";
        final SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(message.toArray());
        jdbcTemplate.batchUpdate(sql, batch);
    }

    @Override
    public void delete(final ChatLang lang, final ChatMessage message) {

        final String sql = "DELETE FROM CHAT_" + lang.val + " WHERE id = :id";
        Map<String, Long> namedParameters = new HashMap<>();
        namedParameters.put("id", message.getId());
        jdbcTemplate.update(sql, namedParameters);

    }

    @Override
    public List<ChatHistoryDto> getChatHistory(ChatLang chatLang) {
        final String sql = "SELECT c.id, c.body, c.message_time, USER.email FROM CHAT_" + chatLang.val +
                " as c INNER JOIN USER ON c.user_id = USER.id ORDER BY c.id DESC";

        return jdbcTemplate.query(sql, (resultSet, i) -> {
            final ChatHistoryDto historyDto = new ChatHistoryDto();
            historyDto.setEmail(resultSet.getString("email"));
            historyDto.setBody(resultSet.getString("body"));
            historyDto.setMessageTime(resultSet.getTimestamp("message_time") != null ?
                    resultSet.getTimestamp("message_time").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    : " ");
            return historyDto;
        });
    }

}
