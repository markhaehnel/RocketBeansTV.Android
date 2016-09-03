package de.markhaehnel.rbtv.rocketbeanstv.events;

import lombok.Getter;

@Getter
public class BufferUpdateEvent {
    private BufferState status;
    public static int BUFFERING_PROGRESS = 0;
    public static int BUFFERING_END = 1;

    public enum BufferState {
        BUFFERING_PROGRESS,
        BUFFERING_END
    }

    public BufferUpdateEvent(BufferState status) {
        this.status = status;
    }
}
