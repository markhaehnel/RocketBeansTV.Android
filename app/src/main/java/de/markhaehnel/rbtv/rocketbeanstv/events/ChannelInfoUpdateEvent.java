package de.markhaehnel.rbtv.rocketbeanstv.events;

import de.markhaehnel.rbtv.rocketbeanstv.objects.RBTV;
import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.ScheduleItem;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import lombok.Getter;

@Getter
public class ChannelInfoUpdateEvent {
    private ScheduleItem scheduleItem;
    private RBTV rbtv;
    private EventStatus status;

    public ChannelInfoUpdateEvent(EventStatus status) {
        this.status = status;
    }

    public ChannelInfoUpdateEvent(ScheduleItem scheduleItem, RBTV rbtv, EventStatus status) {
        this.scheduleItem = scheduleItem;
        this.rbtv = rbtv;
        this.status = status;
    }
}
