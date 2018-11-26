package me.exrates.service;

import com.google.common.collect.EvictingQueue;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.ChatHistoryDto;
import org.springframework.stereotype.Service;

import java.util.Queue;

@Service
@Log4j2(topic = "message_notify")
public class TelegramChatBotService {

    public final static Integer COUNT_OF_MESSAGE_FOR_VIEW = 30;
    private final Queue<ChatHistoryDto> messages = EvictingQueue.create(COUNT_OF_MESSAGE_FOR_VIEW);

    public Queue<ChatHistoryDto> getMessages() {
        return messages;
    }

}
