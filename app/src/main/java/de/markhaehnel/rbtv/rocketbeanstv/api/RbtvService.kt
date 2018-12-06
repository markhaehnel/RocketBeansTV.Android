package de.markhaehnel.rbtv.rocketbeanstv.api

import androidx.lifecycle.LiveData
import de.markhaehnel.rbtv.rocketbeanstv.vo.Stream
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST API access points
 */
interface RbtvService {
    @GET("stream")
    fun getStream(): LiveData<ApiResponse<Stream>>
}