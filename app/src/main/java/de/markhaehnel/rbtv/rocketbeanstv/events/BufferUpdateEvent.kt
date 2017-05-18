package de.markhaehnel.rbtv.rocketbeanstv.events

class BufferUpdateEvent(val status: BufferUpdateEvent.BufferState) {

    enum class BufferState {
        BUFFERING_PROGRESS,
        BUFFERING_END
    }

    companion object {
        var BUFFERING_PROGRESS = 0
        var BUFFERING_END = 1
    }
}
