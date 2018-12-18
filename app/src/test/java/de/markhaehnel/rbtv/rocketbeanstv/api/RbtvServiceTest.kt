package de.markhaehnel.rbtv.rocketbeanstv.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.markhaehnel.rbtv.rocketbeanstv.util.LiveDataCallAdapterFactory
import de.markhaehnel.rbtv.rocketbeanstv.util.LiveDataTestUtil.getValue
import de.markhaehnel.rbtv.rocketbeanstv.vo.ScheduleItem
import de.markhaehnel.rbtv.rocketbeanstv.vo.Stream
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
    fun getStream() {
        enqueueResponse("stream.json")
        val stream = (getValue(service.getStream()) as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/stream"))

        assertThat<Stream>(stream, notNullValue())
        assertThat(stream.videoId, `is`("fIobsq6W33U"))
        assertThat(stream.viewerCount, `is`(2142))
        assertThat(stream.cameras[0], `is`("fIobsq6W33U"))
    }

    @Test
    fun getCurrentShow() {
        enqueueResponse("schedule-current.json")
        val show = (getValue(service.getCurrentShow()) as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/schedule/current"))

        assertThat<ScheduleItem>(show, notNullValue())
        assertThat(show.id, `is`(30102))
        assertThat(show.title, `is`("Hängi Hauptquartier"))
        assertThat(show.topic, `is`("mit Sandro"))
        assertThat(show.show, `is`("Hängi Hauptquartier"))
        //TODO: test the time
        //assertThat(show.timeStart, `is`("2018-12-13T14:00:00+01:00"))
        //assertThat(show.timeEnd, `is`("2018-12-13T17:00:00+01:00"))
        assertThat(show.length, `is`(10800))
        assertThat(show.type, `is`("live"))
        assertThat(show.game, `is`("GRIS"))
    }

    @Test
    fun getUpcomingShows() {
        enqueueResponse("schedule-next-5.json")
        val schedule = (getValue(service.getUpcomingShows()) as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/schedule/next/5"))

        assertThat(schedule.items.size, `is`(5))

        val show = schedule.items[0]
        assertThat(show.id, `is`(30102))
        assertThat(show.title, `is`("Hängi Hauptquartier"))
        //TODO: test the time
        //assertThat(show.timeStart, `is`("2018-12-13T14:00:00+01:00"))
        assertThat(show.type, `is`("live"))
        assertThat(show.game, `is`("GRIS"))

        val show2 = schedule.items[1]
        assertThat(show2.length, `is`(10800))
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