package com.alexcorp.springspirit.annotation.keyboard;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InlineButton {

    String value();

    String data() default "";
    String url() default "";
    boolean pay() default false;

}