package com.apxy.courseSystem.entity.event;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
@ToString
public class UpdateWrongSubjectEvent extends ApplicationEvent {
    private String userName;

    public UpdateWrongSubjectEvent(String userName, Object source) {
        super(source);
        this.userName = userName;
    }
}
