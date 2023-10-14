package io.humanode.humanode.utils;

import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CustomSpringEvent extends ApplicationEvent {
    private final String message;

    public CustomSpringEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
}
