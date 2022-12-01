package com.apxy.courseSystem.entity.event;


import com.apxy.courseSystem.entity.MemberEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

@Getter
@Setter
@ToString
public class MemberEvent extends ApplicationEvent {
    private MemberEntity memberEntity;

    public MemberEvent(MemberEntity memberEntity, Object source)
    {
        super(source);
        this.memberEntity = memberEntity;
    }
}
