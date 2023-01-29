package com.alexcorp.springspirit.core.bpp;

import com.alexcorp.springspirit.annotation.keyboard.InlineButton;
import com.alexcorp.springspirit.annotation.keyboard.InlineKeyboard;
import com.alexcorp.springspirit.annotation.keyboard.Row;
import com.alexcorp.springspirit.core.KeyboardHandlerContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.comparator.Comparators;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.lang.reflect.Method;
import java.util.*;

@Component
@RequiredArgsConstructor
public class BotKeyboardBeanPostProcessor implements BeanPostProcessor {

    private final Environment environment;
    private final KeyboardHandlerContext handlerContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        InlineKeyboard keyboardAnnotation = bean.getClass().getAnnotation(InlineKeyboard.class);
        if (keyboardAnnotation == null) return bean;


        Object kb = buildInlineKeyboard(bean, beanName);
        return kb;
    }

    private InlineKeyboardMarkup buildInlineKeyboard(Object keyboardSchema, String keyboardId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        Map<Integer, List<InlineKeyboardButton>> buttons = new HashMap<>();
        int currRow = 1;
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(keyboardSchema.getClass());
        for (int i = methods.length - 1; i >= 0; i--) {
            Method method = methods[i];
            InlineButton buttonAnnot = method.getAnnotation(InlineButton.class);
            if (buttonAnnot == null) continue;

            Row rowAnnot = method.getAnnotation(Row.class);
            if (rowAnnot != null) {
                currRow = rowAnnot.value();
                if (rowAnnot.value() == Integer.MAX_VALUE) {
                    currRow = methods.length;
                }
            }

            List<InlineKeyboardButton> row = buttons.computeIfAbsent(currRow - 1, k -> new ArrayList<>());

            InlineKeyboardButton button = buildButton(keyboardId, buttonAnnot);
            row.add(button);

            handlerContext.addHandler(button.getCallbackData(), keyboardSchema, method);
        }
        keyboard.setKeyboard(new ArrayList<>(buttons.values()));

        return keyboard;
    }

    private InlineKeyboardButton buildButton(String keyboardId, InlineButton buttonAnnot) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonAnnot.value());

        if (buttonAnnot.url().isEmpty()) {
            if (!buttonAnnot.data().isEmpty()) {
                button.setCallbackData(keyboardId + "_" + buttonAnnot.data());
            } else {
                button.setCallbackData(keyboardId + "_" + buttonAnnot.value()
                        .replaceAll(" ", "_")
                        .toLowerCase());
            }
        } else {
            String url = buttonAnnot.url();
            if (url.startsWith("${") && url.endsWith("}")) {
                String propertyName = url.substring(2, url.length() - 1);
                url = environment.getProperty(propertyName);
            }

            button.setUrl(url);
        }

        if (buttonAnnot.pay()) {
            button.setPay(true);
        }

        return button;
    }
}
