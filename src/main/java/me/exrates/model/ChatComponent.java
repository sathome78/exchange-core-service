package me.exrates.model;

import me.exrates.model.main.ChatMessage;

import java.util.NavigableSet;
import java.util.concurrent.locks.ReadWriteLock;

public class ChatComponent {

    private final ReadWriteLock lock;
    private NavigableSet<ChatMessage> cache;
    private ChatMessage tail;

    public ChatComponent(final ReadWriteLock lock, final NavigableSet<ChatMessage> cache) {
        this.lock = lock;
        this.cache = cache;
    }

    public ReadWriteLock getLock() {
        return lock;
    }

    public NavigableSet<ChatMessage> getCache() {
        return cache;
    }

    public void setCache(NavigableSet<ChatMessage> cache) {
        this.cache = cache;
    }

    public ChatMessage getTail() {
        return tail;
    }

    public void setTail(ChatMessage tail) {
        this.tail = tail;
    }
}
