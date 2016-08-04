package de.markhaehnel.rbtv.rocketbeanstv.events;

import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import lombok.Getter;

@Getter
public class InternetCheckEvent {
    private EventStatus status;

    public InternetCheckEvent(EventStatus status) {
        this.status = status;
    }
}
