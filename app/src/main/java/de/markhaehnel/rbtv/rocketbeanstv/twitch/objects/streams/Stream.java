
package de.markhaehnel.rbtv.rocketbeanstv.twitch.objects.streams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stream {

    @SerializedName("_id")
    @Expose
    public Long id;
    @SerializedName("game")
    @Expose
    public String game;
    @SerializedName("viewers")
    @Expose
    public Long viewers;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("video_height")
    @Expose
    public Long videoHeight;
    @SerializedName("average_fps")
    @Expose
    public Float averageFps;
    @SerializedName("delay")
    @Expose
    public Long delay;
    @SerializedName("is_playlist")
    @Expose
    public Boolean isPlaylist;
    @SerializedName("_links")
    @Expose
    public Links links;
    @SerializedName("preview")
    @Expose
    public Preview preview;
    @SerializedName("channel")
    @Expose
    public Channel channel;

}
