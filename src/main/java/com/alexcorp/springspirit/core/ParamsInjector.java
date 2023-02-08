package com.alexcorp.springspirit.core;

import com.alexcorp.springspirit.annotation.CommandVar;
import com.alexcorp.springspirit.core.session.BotSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ParamsInjector {

    private final ApplicationContext appCtx;

    public Object[] getMethodParamValues(Method method,
                                         Update update,
                                         BotSession session,
                                         Map<String, String> vars) {
        Message msg = update.getMessage();
        if (msg == null) {
            msg = update.getCallbackQuery().getMessage();
        }

        Parameter[] parameters = method.getParameters();
        Class<?>[] paramTypes = method.getParameterTypes();
        Annotation[][] paramAnnots = method.getParameterAnnotations();
        Object[] paramValues = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            if (paramType.equals(Update.class)) {
                paramValues[i] = update;
                continue;
            }

            if (paramType.equals(Message.class)) {
                paramValues[i] = msg;
                continue;
            }

            if (paramType.equals(Document.class)) {
                paramValues[i] = msg.getDocument();
                continue;
            }

            if (paramType.equals(SendMessage.class)) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(msg.getChatId().toString());

                paramValues[i] = sendMessage;
                continue;
            }

            if (paramType.equals(EditMessageReplyMarkup.class)) {
                EditMessageReplyMarkup editMessage = new EditMessageReplyMarkup();
                editMessage.setChatId(msg.getChatId().toString());
                editMessage.setMessageId(msg.getMessageId());

                paramValues[i] = editMessage;
                continue;
            }

            if (paramType.equals(SendPoll.class)) {
                SendPoll sendPoll = new SendPoll();
                sendPoll.setChatId(msg.getChatId().toString());

                paramValues[i] = sendPoll;
                continue;
            }

            if (paramType.equals(ForwardMessage.class)) {
                ForwardMessage forwardMessage = new ForwardMessage();
                forwardMessage.setChatId(msg.getChatId().toString());

                paramValues[i] = forwardMessage;
                continue;
            }

            if (paramType.equals(BotSession.class)) {
                paramValues[i] = session;
                continue;
            }

            if (paramAnnots[i] != null && paramAnnots[i].length != 0) {
                for (Annotation annotation : paramAnnots[i]) {
                    if (annotation.annotationType().isAnnotationPresent(Component.class)) {
                        Map<String, Object> annotAttrs = AnnotationUtils.getAnnotationAttributes(annotation);
                        String value = (String) annotAttrs.get("value");
                        paramValues[i] = appCtx.getBean(value);
                        break;
                    }

                    if (annotation.annotationType() == CommandVar.class) {
                        String varName = parameters[i].getName();
                        String varValue = vars.get(varName);
                        paramValues[i] = varValue;
                        break;
                    }
                }

            } else {
                paramValues[i] = appCtx.getBean(paramType);
            }
        }

        return paramValues;
    }
}
