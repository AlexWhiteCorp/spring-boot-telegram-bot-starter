package com.alexcorp.springspirit.core.worker;

import com.alexcorp.springspirit.core.KeyboardHandlerContext;
import com.alexcorp.springspirit.core.MessageHandlerContext;
import com.alexcorp.springspirit.core.RequestContext;
import com.alexcorp.springspirit.core.bot.TelegramBot;
import com.alexcorp.springspirit.core.session.BotSession;
import com.alexcorp.springspirit.core.session.SessionService;
import datadog.trace.api.DDTags;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class UpdateHandlerWorker implements Runnable {

    private static int ID_COUNTER = 1;

    private final int workerId = ID_COUNTER++;

    private final TelegramBot bot;
    private final MessageHandlerContext msgHandlerCtx;
    private final KeyboardHandlerContext keyboardHandlerCtx;

    private final SessionService sessionService;

    private final RequestContext requestContext;

    @Override
    public void run() {
        Thread.currentThread().setName("Msg handler " + workerId + " ");
        log.debug("Worker {} started", workerId);

        while (true) {
            try {
                handle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handle() {
        Tracer tracer = GlobalTracer.get();
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan("HandlerWorker")
                .withTag(DDTags.SERVICE_NAME, "handle-worker")
                .withTag("resource.name", "handle-update")
                .withTag("worker.id", workerId);

        Update update = waitForUpdate();
        Span span = spanBuilder.start();

        try (io.opentracing.Scope scope = tracer.activateSpan(span)) {
            Optional.ofNullable(handleUpdate(update))
                    .ifPresent(this::send);
        } finally {
            span.finish();
        }
    }

    private Update waitForUpdate() {
        try {
            return bot.getMessagesQueue().take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Object handleUpdate(Update update) {
        try {
            Object result = null;

            if (update.getCallbackQuery() != null) {
                Message msg = update.getCallbackQuery().getMessage();
                BotSession session = sessionService.loadUserSession(msg.getChatId(), msg.getFrom().getId());
                requestContext.initRequest(session);
                result = keyboardHandlerCtx.handle(update);
            }

            if (update.getMessage() != null) {
                Message msg = update.getMessage();
                BotSession session = sessionService.loadUserSession(msg.getChatId(), msg.getFrom().getId());
                requestContext.initRequest(session);
                result = msgHandlerCtx.handle(update);
            }

            return result;
        } finally {
            sessionService.flushSession(requestContext.getSession());
            requestContext.clearSession();
        }
    }

    private void send(Object result) {
        if (result instanceof BotApiMethod send) {
            try {
                bot.send(send);
            } catch (TelegramApiException e) {
                log.info("Msg sending error:");
                e.printStackTrace();
            }
        }
    }
}
