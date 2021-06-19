package de.markhaehnel.rbtv.rocketbeanstv.api

import androidx.lifecycle.LiveData
import de.markhaehnel.rbtv.rocketbeanstv.util.Constants
import de.markhaehnel.rbtv.rocketbeanstv.vo.TwitchAccesToken
import de.markhaehnel.rbtv.rocketbeanstv.vo.TwitchGraphQLAccessTokenBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TwitchGraphQLService {
    @POST("gql")
    @Headers("Client-ID: ${Constants.TWITCH_CLIENT_ID}")
    fun getAccessToken(
        @Body body: TwitchGraphQLAccessTokenBody = TwitchGraphQLAccessTokenBody()
    ): LiveData<ApiResponse<TwitchAccesToken>>
}