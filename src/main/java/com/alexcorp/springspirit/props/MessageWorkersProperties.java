package com.alexcorp.springspirit.props;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class MessageWorkersProperties {

    @Min(1) @Max(128)
    private Integer count = 4;

}
