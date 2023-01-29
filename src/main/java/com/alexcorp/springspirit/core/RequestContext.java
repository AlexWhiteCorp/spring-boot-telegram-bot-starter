package com.alexcorp.springspirit.core;

import com.alexcorp.springspirit.core.session.BotSession;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Scope("thread")
@Component
public class RequestContext {

    private BotSession session;

    public void initRequest(BotSession session) {
        this.session = session;
    }

    public void clearSession() {
        session = null;
    }

}
