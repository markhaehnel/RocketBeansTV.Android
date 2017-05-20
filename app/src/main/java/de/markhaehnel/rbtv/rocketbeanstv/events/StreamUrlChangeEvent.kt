package de.markhaehnel.rbtv.rocketbeanstv.events

import de.markhaehnel.rbtv.rocketbeanstv.objects.Stream
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus

class StreamUrlChangeEvent {
    var streams: List<Stream> = listOf<Stream>()
    var status: EventStatus? = null

    constructor(status: EventStatus) {
        this.status = status
    }

    constructor(streams: List<Stream>, status: EventStatus) {
        this.streams = streams
        this.status = status
    }
}
