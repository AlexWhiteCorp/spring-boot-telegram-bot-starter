package com.alexcorp.springspirit.core.session;

public interface BotSession {

    Class<?> getState();
    void setState(Class<?> state);
    <T, R> R getProp(T key);
    <T, R> void setProp(T key, R value);

}
