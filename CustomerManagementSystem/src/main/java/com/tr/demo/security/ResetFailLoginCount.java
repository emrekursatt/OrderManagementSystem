package com.tr.demo.security;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
@Builder
public class ResetFailLoginCount extends ApplicationEvent {

    private Object source;
    private Long userId;

    public ResetFailLoginCount(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
