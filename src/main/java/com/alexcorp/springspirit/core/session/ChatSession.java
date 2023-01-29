package com.alexcorp.springspirit.core.session;

import java.util.HashMap;
import java.util.Map;

public class ChatSession implements BotSession {

    private Long chatId;
    private Class<?> state;
    private Map<Object, Object> props = new HashMap<>();

    public ChatSession(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    @Override
    public Class<?> getState() {
        return state;
    }

    @Override
    public void setState(Class<?> state) {
        this.state = state;
    }

    @Override
    public <T, R> R getProp(T key) {
        return (R) props.get(key);
    }

    @Override
    public <T, R> void setProp(T key, R value) {
        props.put(key, value);
    }
}
