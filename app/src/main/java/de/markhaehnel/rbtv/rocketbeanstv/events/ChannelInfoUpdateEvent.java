package de.markhaehnel.rbtv.rocketbeanstv.events;

import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import lombok.Getter;

@Getter
public class ChannelInfoUpdateEvent {
    private String currentShow;
    private String viewerCount;
    private EventStatus status;

    public ChannelInfoUpdateEvent(EventStatus status) {
        this.status = status;
    }

    public ChannelInfoUpdateEvent(String currentShow, String viewerCount, EventStatus status) {
        this.currentShow = currentShow;
        this.viewerCount = viewerCount;
        this.status = status;
    }
}
