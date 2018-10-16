package me.exrates.service.impl;

import javafx.util.Pair;
import me.exrates.dao.ChatDao;
import me.exrates.model.ChatComponent;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.main.ChatMessage;
import me.exrates.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Service
public class ChatServiceImpl implements ChatService {
    private final int MESSAGE_BARRIER = 50;
    private final boolean INCLUSIVE = true;

    private final ChatDao chatDao;
    private final EnumMap<ChatLang, ChatComponent> chats;

    private AtomicLong GENERATOR;
    private long flushCursor;


    @Autowired
    public ChatServiceImpl(final ChatDao chatDao,
                           final EnumMap<ChatLang, ChatComponent> chats) {
        this.chatDao = chatDao;
        this.chats = chats;
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
