package com.alexcorp.springspirit.core.session;

import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    @Transactional(readOnly = true)
    public BotSession loadUserSession(long chatId, long userId) {
        return sessionRepository.findByChatIdAndUserId(chatId, userId)
                .orElseGet(() -> new SessionEntity(chatId, userId));
    }

    @Transactional
    public SessionEntity flushSession(BotSession session) {
        if(session == null) {
            return null;
        }

        SessionEntity sessionEntity = (SessionEntity) session;

        return sessionRepository.save(sessionEntity);
    }

}
