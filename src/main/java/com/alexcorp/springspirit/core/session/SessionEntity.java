package com.alexcorp.springspirit.core.session;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_session")
@NoArgsConstructor
public class SessionEntity implements BotSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String username;

    private long chatId;
    private long userId;
    private Class<?> state;

    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> properties;

    public SessionEntity(long chatId, long userId) {
        this.id = UUID.randomUUID();
        this.chatId = chatId;
        this.userId = userId;
        this.properties = new HashMap<>();
    }

    @Override
    public <T, R> R getProp(T key) {
        return (R) properties.get(key.toString());
    }

    @Override
    public <T, R> void setProp(T key, R value) {
        properties.put(key.toString(), value);
    }
}
