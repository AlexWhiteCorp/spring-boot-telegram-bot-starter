package com.alexcorp.springspirit.config;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ThreadScopeConfig {

    @Bean
    public static CustomScopeConfigurer yourCustomScopeConfigurer() {
        CustomScopeConfigurer newConfigurer = new CustomScopeConfigurer();
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("thread", new SimpleThreadScope());
        newConfigurer.setScopes(scopes);

        return newConfigurer;
    }

}
