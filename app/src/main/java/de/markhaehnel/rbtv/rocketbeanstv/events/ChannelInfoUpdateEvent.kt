package de.markhaehnel.rbtv.rocketbeanstv.events

import de.markhaehnel.rbtv.rocketbeanstv.objects.RBTV
import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.ScheduleItem
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus

class ChannelInfoUpdateEvent {
    var scheduleItem: ScheduleItem = ScheduleItem()
    var rbtv: RBTV = RBTV()
    var status: EventStatus? = null

    constructor(status: EventStatus) {
        this.status = status
    }

    constructor(scheduleItem: ScheduleItem, rbtv: RBTV, status: EventStatus) {
        this.scheduleItem = scheduleItem
        this.rbtv = rbtv
        this.status = status
    }
}
