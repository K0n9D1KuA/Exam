package com.apxy.courseSystem.entity.event;

import com.apxy.courseSystem.entity.SystemLogEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
@ToString
public class SystemLogEvent extends ApplicationEvent {
    private SystemLogEntity systemLogEntity;

    public SystemLogEvent(Object source, SystemLogEntity systemLogEntity) {
        super(source);
        this.systemLogEntity = systemLogEntity;
    }
}
