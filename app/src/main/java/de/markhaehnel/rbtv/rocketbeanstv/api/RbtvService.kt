package de.markhaehnel.rbtv.rocketbeanstv.api

import androidx.lifecycle.LiveData
import de.markhaehnel.rbtv.rocketbeanstv.vo.RbtvServiceInfo
import de.markhaehnel.rbtv.rocketbeanstv.vo.Schedule
import retrofit2.http.GET
import retrofit2.http.Query

interface RbtvService {
    @GET("v1/frontend/init")
    fun getServiceInfo(): LiveData<ApiResponse<RbtvServiceInfo>>

    @GET("v1/schedule/normalized")
    fun getSchedule(
        @Query("startDay") startDay: Long = System.currentTimeMillis() / 1000L,
        @Query("endDay") endDay: Long? = startDay
    ): LiveData<ApiResponse<Schedule>>
}