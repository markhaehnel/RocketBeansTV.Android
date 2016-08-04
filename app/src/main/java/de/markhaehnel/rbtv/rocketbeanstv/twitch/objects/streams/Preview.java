
package de.markhaehnel.rbtv.rocketbeanstv.twitch.objects.streams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Preview {

    @SerializedName("small")
    @Expose
    public String small;
    @SerializedName("medium")
    @Expose
    public String medium;
    @SerializedName("large")
    @Expose
    public String large;
    @SerializedName("template")
    @Expose
    public String template;

}
