package com.alexcorp.springspirit.core.bot;

import com.alexcorp.springspirit.props.SpringSpiritProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@ConditionalOnProperty(
        name = "spring.spirit.bot.webHook.enable",
        havingValue = "false",
        matchIfMissing = true
)
@Slf4j
@Component
@RequiredArgsConstructor
public class LongPollingBot extends TelegramLongPollingBot implements TelegramBot {

    private final static int RECONNECT_PAUSE = 10_000;

    private final SpringSpiritProperties properties;

    private final BlockingQueue<Update> receivedMessages = new LinkedBlockingDeque<>();

    @PostConstruct
    public void init() throws TelegramApiException {
        checkToken();
        connect();
    }

    @Override
    public void connect() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(this);
            log.info("Telegram Bot @" + properties.getUsername() + " started");
            log.info("Active Mode: Long Polling");
        } catch (Exception e) {
            log.error("Can't Connect. Pause " + RECONNECT_PAUSE / 1000 + " sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }

            connect();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        if (msg != null) {
            log.info("New Message: '{}' in chat [{}]", msg.getText(), msg.getChatId());
        }

        receivedMessages.add(update);
    }

    private void checkToken() {
        if (properties.getToken() == null) {
            log.error("Can't find Bot Token in properties file(spring.spirit.bot.token)!");
            System.exit(1);
        }
    }

    @Override
    public void onClosing() {
        log.info("Bot stopped");
    }

    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }

    @Override
    public BlockingQueue<Update> getMessagesQueue() {
        return receivedMessages;
    }

    @Override
    public <T extends Message, Method extends BotApiMethod<T>> T send(Method method) throws TelegramApiException {
        return execute(method);
    }
}
