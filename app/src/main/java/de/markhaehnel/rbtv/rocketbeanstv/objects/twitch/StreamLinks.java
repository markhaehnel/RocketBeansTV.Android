
package de.markhaehnel.rbtv.rocketbeanstv.objects.twitch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StreamLinks {

    @SerializedName("self")
    @Expose
    public String self;
    @SerializedName("channel")
    @Expose
    public String channel;

}
