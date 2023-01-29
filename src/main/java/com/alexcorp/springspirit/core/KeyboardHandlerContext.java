package com.alexcorp.springspirit.core;

import com.alexcorp.springspirit.core.session.BotSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeyboardHandlerContext implements HandlerContext{

    private final MessageHandlerContext msgHandlerCtx;
    private final RequestContext requestContext;
    private final ParamsInjector paramsInjector;

    private final Map<String, InlineKeyboardHandler> handlers = new HashMap<>();

    public void addHandler(String command, Object keyboardSchema, Method method) {
        InlineKeyboardHandler handler = new InlineKeyboardHandler(keyboardSchema, method);
        handlers.put(command, handler);
    }

    @SneakyThrows
    @Override
    public Object handle(Update update) {
        String command = update.getCallbackQuery().getData();
        Message msg = update.getCallbackQuery().getMessage();
        InlineKeyboardHandler handler = handlers.get(command);

        if(handler == null) {
            return msgHandlerCtx.handle(update, command);
            //throw new UpdateHandlerNotFoundException(msg.getText(), null);
        }

        BotSession session = requestContext.getSession();
        Object[] paramValues = paramsInjector.getMethodParamValues(handler.getMethod(), update, session, null);

        Object result = handler.invoke(paramValues);
        if(result instanceof Redirect redirect) {
            return msgHandlerCtx.handle(update, redirect.getCommand());
        }

        return result;
    }

}
