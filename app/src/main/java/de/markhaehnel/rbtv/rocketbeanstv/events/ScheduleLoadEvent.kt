package de.markhaehnel.rbtv.rocketbeanstv.events

import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.ScheduleItem
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus

class ScheduleLoadEvent {
    var shows: List<ScheduleItem>? = null
    var status: EventStatus? = null

    constructor(status: EventStatus) {
        this.status = status
    }

    constructor(shows: List<ScheduleItem>, status: EventStatus) {
        this.shows = shows
        this.status = status
    }
}
