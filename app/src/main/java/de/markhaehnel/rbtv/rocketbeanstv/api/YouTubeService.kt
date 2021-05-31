package de.markhaehnel.rbtv.rocketbeanstv.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface YouTubeService {
    @GET("get_video_info?html5=1")
    fun getVideoInfo(@Query("video_id") videoId: String): Call<ResponseBody>

    @GET("")
    fun getPlaylist(@Url url: String): Call<ResponseBody>
}