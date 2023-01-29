package com.alexcorp.springspirit.props;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class WebHookBotProperties {

    private boolean enable = false;
    private String url;

}
