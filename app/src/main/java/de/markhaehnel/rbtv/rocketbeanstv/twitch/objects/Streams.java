
package de.markhaehnel.rbtv.rocketbeanstv.twitch.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import de.markhaehnel.rbtv.rocketbeanstv.twitch.objects.streams.Links__;
import de.markhaehnel.rbtv.rocketbeanstv.twitch.objects.streams.Stream;

public class Streams {

    @SerializedName("stream")
    @Expose
    public Stream stream;
    @SerializedName("_links")
    @Expose
    public Links__ links;

}
