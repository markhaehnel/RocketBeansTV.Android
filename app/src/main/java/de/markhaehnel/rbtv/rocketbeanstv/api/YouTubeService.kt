package de.markhaehnel.rbtv.rocketbeanstv.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeService {
    @GET("get_video_info")
    fun getRawStreamData(@Query("video_id") videoId: String): Call<ResponseBody>
}