package com.alexcorp.springspirit.annotation.keyboard;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Keyboard {

    String value() default "";

    boolean selective() default false;
    boolean resize() default true;
    boolean oneTime() default false;
}
