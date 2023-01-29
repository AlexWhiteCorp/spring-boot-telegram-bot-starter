package com.alexcorp.springspirit.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class InlineKeyboardHandler {

    private Object keyboardSchema;
    private Method method;

    public Object invoke(Object[] params) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(keyboardSchema, params);
    }
}
