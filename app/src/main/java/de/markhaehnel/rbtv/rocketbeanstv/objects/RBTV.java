package de.markhaehnel.rbtv.rocketbeanstv.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class RBTV {

    @SerializedName("videoId")
    @Expose
    public String videoId;
    @SerializedName("viewerCount")
    @Expose
    public String viewerCount;
    @SerializedName("error")
    @Expose
    public Object error;

}