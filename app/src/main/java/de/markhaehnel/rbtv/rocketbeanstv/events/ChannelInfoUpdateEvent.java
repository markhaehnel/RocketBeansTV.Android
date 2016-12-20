package de.markhaehnel.rbtv.rocketbeanstv.events;

import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.ScheduleItem;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import lombok.Getter;

@Getter
public class ChannelInfoUpdateEvent {
    private ScheduleItem scheduleItem;
    private EventStatus status;

    public ChannelInfoUpdateEvent(EventStatus status) {
        this.status = status;
    }

    public ChannelInfoUpdateEvent(ScheduleItem scheduleItem, EventStatus status) {
        this.scheduleItem = scheduleItem;
        this.status = status;
    }
}
