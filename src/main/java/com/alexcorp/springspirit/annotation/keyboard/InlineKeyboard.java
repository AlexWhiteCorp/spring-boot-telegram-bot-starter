package com.alexcorp.springspirit.annotation.keyboard;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface InlineKeyboard {

    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";
}
