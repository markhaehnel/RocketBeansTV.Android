
package de.markhaehnel.rbtv.rocketbeanstv.objects.twitch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StreamsLinks {

    @SerializedName("self")
    @Expose
    public String self;
    @SerializedName("follows")
    @Expose
    public String follows;
    @SerializedName("commercial")
    @Expose
    public String commercial;
    @SerializedName("stream_key")
    @Expose
    public String streamKey;
    @SerializedName("chat")
    @Expose
    public String chat;
    @SerializedName("features")
    @Expose
    public String features;
    @SerializedName("subscriptions")
    @Expose
    public String subscriptions;
    @SerializedName("editors")
    @Expose
    public String editors;
    @SerializedName("teams")
    @Expose
    public String teams;
    @SerializedName("videos")
    @Expose
    public String videos;

}
