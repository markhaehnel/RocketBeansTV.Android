package de.markhaehnel.rbtv.rocketbeanstv.events;

import java.util.ArrayList;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import de.markhaehnel.rbtv.rocketbeanstv.utils.ScheduleShow;
import lombok.Getter;

@Getter
public class ScheduleLoadEvent {
    private ArrayList<ScheduleShow> shows;
    private EventStatus status;

    public ScheduleLoadEvent(EventStatus status) {
        this.status = status;
    }

    public ScheduleLoadEvent(ArrayList<ScheduleShow> shows, EventStatus status) {
        this.shows = shows;
        this.status = status;
    }
}
