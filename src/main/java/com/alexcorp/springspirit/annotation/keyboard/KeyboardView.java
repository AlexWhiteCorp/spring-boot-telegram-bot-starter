package com.alexcorp.springspirit.annotation.keyboard;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KeyboardView {

    Class[] value() default {};

}
