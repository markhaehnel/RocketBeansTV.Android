package de.markhaehnel.rbtv.rocketbeanstv.repository

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.markhaehnel.rbtv.rocketbeanstv.AppExecutors
import de.markhaehnel.rbtv.rocketbeanstv.api.RbtvService
import de.markhaehnel.rbtv.rocketbeanstv.api.YouTubeService
import de.markhaehnel.rbtv.rocketbeanstv.vo.Resource
import de.markhaehnel.rbtv.rocketbeanstv.vo.Stream
import de.markhaehnel.rbtv.rocketbeanstv.vo.StreamDataRaw
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
    private val rbtvService: RbtvService,
    private val youTubeService: YouTubeService
) {

    fun loadStream(): LiveData<Resource<Stream>> {
        val data = MutableLiveData<Resource<Stream>>()

        rbtvService.getStream().enqueue(object : Callback<Stream> {
            override fun onResponse(call: Call<Stream>?, response: Response<Stream>?) {
                data.value = Resource.success(response?.body())
            }

            override fun onFailure(call: Call<Stream>?, t: Throwable?) {
                data.value = Resource.error(t.toString())
            }
        })

        return data
    }

    fun loadStreamDataRaw(videoId: String): LiveData<Resource<StreamDataRaw>> {
        val data = MutableLiveData<Resource<StreamDataRaw>>()

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

                val dataRaw = StreamDataRaw(hlsUrl.toUri())
                data.value = Resource.success(dataRaw)
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                data.value = Resource.error(t.toString())
            }
        })

        return data
    }
}