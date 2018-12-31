package de.markhaehnel.rbtv.rocketbeanstv.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.markhaehnel.rbtv.rocketbeanstv.util.LiveDataCallAdapterFactory
import de.markhaehnel.rbtv.rocketbeanstv.util.LiveDataTestUtil.getValue
import de.markhaehnel.rbtv.rocketbeanstv.vo.RbtvServiceInfo
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsNull.notNullValue
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

@RunWith(JUnit4::class)
class RbtvServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: RbtvService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(RbtvService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getServiceInfo() {
        enqueueResponse("init.json")
        val serviceInfo = (getValue(service.getServiceInfo()) as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/v1/frontend/init"))

        assertThat<RbtvServiceInfo>(serviceInfo, notNullValue())

        val streamInfo = serviceInfo.service.streamInfo
        assertThat(streamInfo.youtubeToken, `is`("1wbqvTGnd3w"))
        assertThat(streamInfo.twitchChannel, `is`("rocketbeanstv"))

        val webSocket = serviceInfo.service.webSocket
        assertThat(webSocket.url, `is`("https://api.rocketbeans.tv/"))
        assertThat(webSocket.path, `is`("/socket.io"))

        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        df.timeZone = TimeZone.getTimeZone("GMT")

        val showInfo = streamInfo.showInfo
        assertThat(showInfo.progress, `is`(79.98837037037038))
        assertThat(showInfo.showId, `is`(90))
        assertThat(showInfo.timeStart, `is`(df.parse("2018-12-20T09:30:00.000Z")))
        assertThat(showInfo.timeEnd, `is`(df.parse("2018-12-20T10:15:00.000Z")))
        assertThat(showInfo.title, `is`("MoinMoin #988"))
        assertThat(showInfo.topic, `is`("Die Morning-Show mit Etienne"))
        assertThat(showInfo.type, `is`("live"))

        val viewers = streamInfo.showInfo.viewers
        assertThat(viewers.twitch, `is`(995))
        assertThat(viewers.youtube, `is`(2470))
        assertThat(viewers.total, `is`(3465))
    }

    @Test
    fun getUpcomingShows() {
        enqueueResponse("scheduleSingleDay.json")
        val schedule = (getValue(service.getSchedule(1534024800)) as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/v1/schedule/normalized?startDay=1534024800&endDay=1534024800"))

        assertThat(schedule.days.count(), `is`(1))
        assertThat(schedule.days[0].items.count(), `is`(14))

        val show = schedule.days[0].items[0]
        assertThat(show.id, `is`(26610))
        assertThat(show.title, `is`("Zocken mit Denzel #3"))
        //TODO: test the time
        //assertThat(show.timeStart, `is`("2018-08-12T10:40:00.000Z"))
        assertThat(show.type, `is`("rerun"))

        val show2 = schedule.days[0].items[1]
        assertThat(show2.duration, `is`(5682))
    }


    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader
            .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}