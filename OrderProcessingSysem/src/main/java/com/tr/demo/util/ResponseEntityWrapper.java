package com.tr.demo.util;

import com.tr.demo.model.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEntityWrapper<T> {
    private ResponseEntity<BaseResponse<T>> responseEntity;

    public ResponseEntityWrapper(BaseResponse<T> baseResponse, HttpStatus status) {
        this.responseEntity = ResponseEntity.status(status).body(baseResponse);
    }
}
