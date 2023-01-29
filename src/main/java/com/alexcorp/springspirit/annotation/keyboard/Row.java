package com.alexcorp.springspirit.annotation.keyboard;

import javax.validation.constraints.Min;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Row {

    @Min(0)
    int value();

}