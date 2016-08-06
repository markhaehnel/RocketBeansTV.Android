
package de.markhaehnel.rbtv.rocketbeanstv.objects.twitch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Streams {

    @SerializedName("stream")
    @Expose
    public Stream stream;
    @SerializedName("_links")
    @Expose
    public StreamLinks links;

}
