package me.exrates.service;

import me.exrates.exception.IllegalChatMessageException;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.main.ChatMessage;

import java.util.Set;

public interface ChatService {
    Set<ChatMessage> getLastMessages(ChatLang toInstance);

    ChatMessage persistPublicMessage(String simpleMessage, String email, ChatLang chatLang) throws IllegalChatMessageException;
}
