package de.markhaehnel.rbtv.rocketbeanstv.events

import de.markhaehnel.rbtv.rocketbeanstv.objects.Stream
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus

class StreamUrlChangeEvent {
    var stream: Stream? = null
    var videoId: String? = null
    var status: EventStatus? = null

    constructor(status: EventStatus) {
        this.status = status
    }

    constructor(stream: Stream, videoId: String, status: EventStatus) {
        this.stream = stream
        this.videoId = videoId
        this.status = status
    }
}
