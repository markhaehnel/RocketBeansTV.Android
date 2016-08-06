package de.markhaehnel.rbtv.rocketbeanstv.objects.twitch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccessToken {
    @SerializedName("token")
    @Expose
    public String token;
    @SerializedName("sig")
    @Expose
    public String sig;
    @SerializedName("mobile_restricted")
    @Expose
    public Boolean mobileRestricted;
}