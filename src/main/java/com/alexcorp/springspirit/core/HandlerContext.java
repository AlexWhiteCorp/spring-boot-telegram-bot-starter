package com.alexcorp.springspirit.core;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface HandlerContext {

    Object handle(Update update);

}
