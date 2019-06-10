package de.markhaehnel.rbtv.rocketbeanstv.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.markhaehnel.rbtv.rocketbeanstv.util.LiveDataCallAdapterFactory
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
class YouTubeServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: YouTubeService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(YouTubeService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getVideoInfo() {
        enqueueResponse("videoinfo")
        val videoInfo = service.getVideoInfo("20lATPvwNmE").execute().body()

        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/get_video_info?video_id=20lATPvwNmE"))

        assertThat(videoInfo, notNullValue())
    }

    @Test
    fun getPlaylist() {
        enqueueResponse("playlist.m3u8")

        val path = "/myCustomPath"
        val requestUrl = mockWebServer.url(path)
        val videoInfo = service.getPlaylist(requestUrl.toString()).execute().body()

        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`(path))

        assertThat(videoInfo, notNullValue())
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