package de.markhaehnel.rbtv.rocketbeanstv.events;

import java.util.List;

import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.ScheduleItem;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import lombok.Getter;

@Getter
public class ScheduleLoadEvent {
    private List<ScheduleItem> shows;
    private EventStatus status;

    public ScheduleLoadEvent(EventStatus status) {
        this.status = status;
    }

    public ScheduleLoadEvent(List<ScheduleItem> shows, EventStatus status) {
        this.shows = shows;
        this.status = status;
    }
}
