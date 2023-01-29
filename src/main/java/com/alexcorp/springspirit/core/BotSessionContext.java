package com.alexcorp.springspirit.core;

import com.alexcorp.springspirit.core.session.BotSession;
import com.alexcorp.springspirit.core.session.ChatSession;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BotSessionContext {

    private final Map<Long, BotSession> sessions = new HashMap<>();

    public BotSession getSession(Long sessionId) {
        BotSession session = sessions.get(sessionId);

        if(session == null) {
            session = new ChatSession(sessionId);
            sessions.put(sessionId, session);
        }

        return session;
    }
}
