package de.markhaehnel.rbtv.rocketbeanstv.api

import androidx.lifecycle.LiveData
import de.markhaehnel.rbtv.rocketbeanstv.util.Constants
import de.markhaehnel.rbtv.rocketbeanstv.vo.TwitchAccesToken
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TwitchUsherService {
    @GET("api/channel/hls/${Constants.TWITCH_CHANNEL}.m3u8?allow_source=true&allow_audio_only=false&client_id=${Constants.TWITCH_CLIENT_ID}")
    fun getPlaylist(
        @Query("token") token: String,
        @Query("sig") signature: String
    ): Call<ResponseBody>
}