package com.apxy.courseSystem.entity.event;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@Getter
@Setter
public class DoneSubjectImageEvent extends ApplicationEvent {
    private String imageUrl;
    private Long currentIndex;
    private String memberName;

    public DoneSubjectImageEvent(String imageUrl, Object source, Long currentIndex, String memberName) {
        super(source);
        this.currentIndex = currentIndex;
        this.imageUrl = imageUrl;
        this.memberName = memberName;
    }
}
