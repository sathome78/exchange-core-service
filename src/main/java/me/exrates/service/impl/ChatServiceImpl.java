package me.exrates.service.impl;

import javafx.util.Pair;
import me.exrates.dao.ChatDao;
import me.exrates.exception.IllegalChatMessageException;
import me.exrates.model.ChatComponent;
import me.exrates.model.User;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.main.ChatMessage;
import me.exrates.service.ChatService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class ChatServiceImpl implements ChatService {
    private final int MESSAGE_BARRIER = 50;
    private final int CACHE_BARRIER = 150;
    private final int MAX_MESSAGE = 256;
    private final boolean INCLUSIVE = true;

    private final ChatDao chatDao;
    private final EnumMap<ChatLang, ChatComponent> chats;

    private AtomicLong GENERATOR;
    private long flushCursor;

    private final Predicate<String> deprecatedChars = Pattern.compile("^[^<>{}&*\"/;`]*$").asPredicate();
    private final UserService userService;

    @Autowired
    public ChatServiceImpl(final ChatDao chatDao,
                           final EnumMap<ChatLang, ChatComponent> chats, UserService userService) {
        this.chatDao = chatDao;
        this.chats = chats;
        this.userService = userService;
    }

    @PostConstruct
    public void cacheWarm() {
        final List<Long> ids = new ArrayList<>();
        Stream.of(ChatLang.values())
                .map(lang -> new Pair<>(lang, new TreeSet<>(chatDao.findLastMessages(lang, MESSAGE_BARRIER))))
                .forEach(pair -> {
                    final ChatComponent comp = chats.get(pair.getKey());
                    final NavigableSet<ChatMessage> cache = pair.getValue();
                    final ChatMessage tail;
                    if (cache.isEmpty()) {
                        tail = null;
                        ids.add(0L);
                    } else {
                        tail = cache.last();
                        ids.add(cache.first().getId());
                    }
                    comp.setCache(cache);
                    comp.setTail(tail);
                });
        GENERATOR = new AtomicLong(ids.stream().reduce(Long::max).orElse(0L));
        flushCursor = GENERATOR.get();
    }

    public NavigableSet<ChatMessage> getLastMessages(final ChatLang lang) {
        final ChatComponent comp = chats.get(lang);
        if (comp.getCache().size() == 0) {
            return new TreeSet<>();
        }
        final NavigableSet<ChatMessage> result;
        try {
            comp.getLock().readLock().lock();
            result = new TreeSet<>(comp.getCache().headSet(comp.getTail(), INCLUSIVE));
        } finally {
            comp.getLock().readLock().unlock();
        }
        return result;
    }

    @Override
    public ChatMessage persistPublicMessage(final String body, final String email, ChatLang lang) throws IllegalChatMessageException {
        if (body.isEmpty() || body.length() > MAX_MESSAGE || !deprecatedChars.test(body)) {
            throw new IllegalChatMessageException("Message contains invalid symbols : " + body);
        }
        User user;
        final ChatMessage message = new ChatMessage();
        if (!isEmpty(email)) {
            try {
                user = userService.findByEmail(email);
                message.setUserId(user.getId());
                message.setNickname(user.getNickname());
            } catch (Exception ex) {
                message.setUserId(0);
                message.setNickname("anonymous");
            }
        } else {
            message.setUserId(0);
            message.setNickname("anonymous");
        }
        message.setBody(body);
        message.setTime(LocalDateTime.now());
//        final ChatComponent comp = chats.get(lang);
//        cacheMessage(message, comp);
        return chatDao.persistPublic(lang, message);
    }


    @Scheduled(fixedDelay = 1000L, initialDelay = 1000L)
    public void flushCache() {
        for (ChatLang lang : ChatLang.values()) {
            final ChatMessage cacheCeil = new ChatMessage();
            cacheCeil.setId(flushCursor);
            final ChatMessage higher = getLastMessages(lang).lower(cacheCeil);
            if (higher != null) {
                final NavigableSet<ChatMessage> newMessages = getLastMessages(lang).headSet(higher, INCLUSIVE);
                chatDao.persist(lang, newMessages);
                final long newFlushCursor = newMessages.first().getId();
                if (flushCursor < newFlushCursor) {
                    flushCursor = newFlushCursor;
                }
            }
        }
    }

}
