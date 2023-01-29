package com.alexcorp.springspirit.exception;

import java.util.Optional;

public class UpdateHandlerNotFoundException extends RuntimeException{

    public UpdateHandlerNotFoundException(String chatMsg, Class<?> state) {
        super(String.format(
                "Can't find handler to process msg '%s' in state '%s'",
                chatMsg,
                Optional.of(state)
                        .map(Class::getSimpleName)
                        .orElse("null"))
        );
    }
}
