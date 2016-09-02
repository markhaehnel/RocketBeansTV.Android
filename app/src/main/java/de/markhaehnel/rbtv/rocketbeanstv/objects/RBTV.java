package de.markhaehnel.rbtv.rocketbeanstv.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class RBTV {

    @SerializedName("cached")
    @Expose
    public Boolean cached;
    @SerializedName("videoId")
    @Expose
    public String videoId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("viewerCount")
    @Expose
    public String viewerCount;
    @SerializedName("error")
    @Expose
    public Object error;

}