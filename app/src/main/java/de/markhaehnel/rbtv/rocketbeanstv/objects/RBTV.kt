package de.markhaehnel.rbtv.rocketbeanstv.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RBTV {
    @SerializedName("cameras")
    @Expose
    var cameras: List<String> = listOf<String>()

    @SerializedName("viewerCount")
    @Expose
    var viewerCount: String = "0"

    @SerializedName("error")
    @Expose
    var error: Any? = null

}