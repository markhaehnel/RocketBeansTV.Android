package de.markhaehnel.rbtv.rocketbeanstv.api

import androidx.lifecycle.LiveData
import de.markhaehnel.rbtv.rocketbeanstv.vo.ScheduleItem
import de.markhaehnel.rbtv.rocketbeanstv.vo.Stream
import retrofit2.http.GET

interface RbtvService {
    @GET("stream")
    fun getStream(): LiveData<ApiResponse<Stream>>

    @GET("schedule/current")
    fun getCurrentSchedule(): LiveData<ApiResponse<ScheduleItem>>

    @GET("schedule/next/5")
    fun getUpcomingShows(): LiveData<ApiResponse<List<ScheduleItem>>>
}