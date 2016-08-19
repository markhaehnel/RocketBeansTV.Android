package de.markhaehnel.rbtv.rocketbeanstv.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoId {

    @SerializedName("cached")
    @Expose
    public Boolean cached;
    @SerializedName("videoId")
    @Expose
    public String videoId;
    @SerializedName("error")
    @Expose
    public Object error;

}