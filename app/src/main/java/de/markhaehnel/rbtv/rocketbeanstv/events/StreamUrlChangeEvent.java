package de.markhaehnel.rbtv.rocketbeanstv.events;

import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import lombok.Getter;

@Getter
public class StreamUrlChangeEvent {
    private String url;
    private EventStatus status;

    public StreamUrlChangeEvent(EventStatus status) {
        this.status = status;
    }

    public StreamUrlChangeEvent(String url, EventStatus status) {
        this.url = url;
        this.status = status;
    }
}
