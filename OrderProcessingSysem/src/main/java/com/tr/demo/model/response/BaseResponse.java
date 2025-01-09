package com.tr.demo.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponse<T> {
    private String message;
    private long timestamp;
    T data;

    public long getTimestamp() {
        return System.currentTimeMillis();
    }
}
