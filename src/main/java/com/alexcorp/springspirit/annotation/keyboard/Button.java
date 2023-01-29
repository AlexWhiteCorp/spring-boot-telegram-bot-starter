package com.alexcorp.springspirit.annotation.keyboard;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Button {

    String value();

    boolean contact() default false;

    boolean location() default false;

}