package de.markhaehnel.rbtv.rocketbeanstv.repository

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import de.markhaehnel.rbtv.rocketbeanstv.AppExecutors
import de.markhaehnel.rbtv.rocketbeanstv.api.*
import de.markhaehnel.rbtv.rocketbeanstv.vo.*
import io.lindstrom.m3u8.model.MasterPlaylist
import io.lindstrom.m3u8.parser.MasterPlaylistParser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLDecoder
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val rbtvService: RbtvService,
    private val twitchGraphQLService: TwitchGraphQLService,
    private val twitchUsherService: TwitchUsherService
) {
    fun loadServiceInfo(): LiveData<Resource<RbtvServiceInfo>> {
        return object : NetworkBoundResource<RbtvServiceInfo>(appExecutors) {
            override fun createCall() = rbtvService.getServiceInfo()
        }.asLiveData()
    }

    fun loadSchedule(timeStart: Long, timeEnd: Long): LiveData<Resource<Schedule>> {
        return object : NetworkBoundResource<Schedule>(appExecutors) {
            override fun createCall() = rbtvService.getSchedule(timeStart, timeEnd)
        }.asLiveData()
    }

    fun loadAccessToken(): LiveData<Resource<TwitchAccesToken>> {
        return object : NetworkBoundResource<TwitchAccesToken>(appExecutors) {
            override fun createCall() = twitchGraphQLService.getAccessToken()
        }.asLiveData()
    }

    fun loadPlaylist(token: String, signature: String): LiveData<Resource<MasterPlaylist>> {
        val data = MutableLiveData<Resource<MasterPlaylist>>()
        data.value = Resource.loading(null)

        //TODO: refactor this into a custom retrofit converter
        twitchUsherService.getPlaylist(token, signature).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    var responseBody = response.body()?.string()

                    if (!responseBody.isNullOrBlank()) {
                        // Hotfix for playlist parser not understanding #EXT-X-TWITCH-INFO attribute
                        val regex = "^#EXT-X-TWITCH-INFO.*$\n".toRegex(RegexOption.MULTILINE)
                        responseBody = responseBody.replace(regex, "")



                        val playlist = MasterPlaylistParser().readPlaylist(responseBody)
                        data.value = Resource.success(playlist)
                    } else {
                        data.value = Resource.error("Error: Playlist is empty")
                    }
                } catch (e: Exception) {
                    data.value = Resource.error("Error while fetching playlist")
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                data.value = Resource.error(t.toString())
            }
        })

        return data
    }
}
