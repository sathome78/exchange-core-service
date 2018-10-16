package me.exrates.dao;

import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.main.ChatMessage;

import java.util.List;
import java.util.NavigableSet;

public interface ChatDao {
    void delete(ChatLang lang, ChatMessage message);

    List<ChatHistoryDto> getChatHistory(ChatLang chatLang);

    void persist(ChatLang lang, NavigableSet<ChatMessage> newMessages);

    List<ChatMessage> findLastMessages(ChatLang lang, int message_barrier);

}
