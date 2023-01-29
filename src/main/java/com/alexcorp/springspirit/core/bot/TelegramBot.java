package com.alexcorp.springspirit.core.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

public interface TelegramBot {

    void connect() throws TelegramApiException;

    BlockingQueue<Update> getMessagesQueue();

    <T extends Message, Method extends BotApiMethod<T>> T send(Method method) throws TelegramApiException;

}
