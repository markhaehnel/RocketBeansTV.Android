package de.markhaehnel.rbtv.rocketbeanstv.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RBTV {
    @SerializedName("videoId")
    @Expose
    var videoId: String? = null

    @SerializedName("viewerCount")
    @Expose
    var viewerCount: String? = null

    @SerializedName("error")
    @Expose
    var error: Any? = null

}