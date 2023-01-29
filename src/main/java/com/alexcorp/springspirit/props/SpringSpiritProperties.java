package com.alexcorp.springspirit.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.spirit.bot")
public class SpringSpiritProperties {

    private String username;
    private String token;

    private MessageWorkersProperties workers;
    private WebHookBotProperties webHook;

}
