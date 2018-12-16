package de.markhaehnel.rbtv.rocketbeanstv.api

import de.markhaehnel.rbtv.rocketbeanstv.vo.Schedule
import de.markhaehnel.rbtv.rocketbeanstv.vo.ScheduleItem
import de.markhaehnel.rbtv.rocketbeanstv.vo.Stream
import retrofit2.Call
import retrofit2.http.GET

interface RbtvService {
    @GET("stream")
    fun getStream(): Call<Stream>

    @GET("schedule/current")
    fun getCurrentShow(): Call<ScheduleItem>

    @GET("schedule/next/5")
    fun getUpcomingShows(): Call<Schedule>
}