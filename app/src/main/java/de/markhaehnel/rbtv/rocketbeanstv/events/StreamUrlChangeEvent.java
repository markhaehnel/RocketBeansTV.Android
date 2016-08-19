package de.markhaehnel.rbtv.rocketbeanstv.events;

import de.markhaehnel.rbtv.rocketbeanstv.objects.Stream;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import lombok.Getter;

@Getter
public class StreamUrlChangeEvent {
    private Stream stream;
    private String videoId;
    private EventStatus status;

    public StreamUrlChangeEvent(EventStatus status) {
        this.status = status;
    }

    public StreamUrlChangeEvent(Stream stream, String videoId, EventStatus status) {
        this.stream = stream;
        this.videoId = videoId;
        this.status = status;
    }
}
