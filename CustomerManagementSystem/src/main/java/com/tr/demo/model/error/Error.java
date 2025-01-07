package com.tr.demo.model.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Error {
    private int code;
    private String message;
    private long timestamp;
}
