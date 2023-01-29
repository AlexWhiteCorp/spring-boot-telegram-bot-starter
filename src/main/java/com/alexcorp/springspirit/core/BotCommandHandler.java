package com.alexcorp.springspirit.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class BotCommandHandler {

    private final String command;
    private final String regex;
    private final Class<?>[] states;
    private final Method method;

}
