package de.markhaehnel.rbtv.rocketbeanstv.repository

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.markhaehnel.rbtv.rocketbeanstv.AppExecutors
import de.markhaehnel.rbtv.rocketbeanstv.api.RbtvService
import de.markhaehnel.rbtv.rocketbeanstv.api.YouTubeService
import de.markhaehnel.rbtv.rocketbeanstv.vo.*
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
    private val youTubeService: YouTubeService
) {
    fun loadServiceInfo(): LiveData<Resource<RbtvServiceInfo>> {
        return object : NetworkBoundResource<RbtvServiceInfo>(appExecutors) {
            override fun createCall() = rbtvService.getServiceInfo()
        }.asLiveData()
    }

    fun loadSchedule(): LiveData<Resource<Schedule>> {
        return object : NetworkBoundResource<Schedule>(appExecutors) {
            override fun createCall() = rbtvService.getSchedule()
        }.asLiveData()
    }

    fun loadStreamManifest(videoId: String): LiveData<Resource<StreamManifest>> {
        //TODO: move this to NetworkBoundResource
        val data = MutableLiveData<Resource<StreamManifest>>()
        data.value = Resource.loading(null)

        youTubeService.getRawStreamData(videoId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                val responseString = response?.body()!!.string()

                val parameters = HashMap<String, String>()
                for (param in responseString.split(Pattern.quote("&").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    val line = param.split(Pattern.quote("=").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (line.size == 2)
                        parameters.put(line[0], URLDecoder.decode(line[1], "UTF-8"))
                }

                val hlsUrl: String = parameters["hlsvp"] as String

                val dataRaw = StreamManifest(hlsUrl.toUri())
                data.value = Resource.success(dataRaw)
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                data.value = Resource.error(t.toString())
            }
        })

        return data
    }
}