package de.markhaehnel.rbtv.rocketbeanstv.objects;

import lombok.Getter;

@Getter
public class Stream {
    String url;
    String bandwidth;
    String resolution;
    String[] availableResolutions;

    public Stream(String url, String bandwidth, String resolution) {
        this.url = url;
        this.bandwidth = bandwidth;
        this.resolution = resolution;
    }

    public void setAvailableResolutions(String[] availableResolutions) {
        this.availableResolutions = availableResolutions;
    }
}