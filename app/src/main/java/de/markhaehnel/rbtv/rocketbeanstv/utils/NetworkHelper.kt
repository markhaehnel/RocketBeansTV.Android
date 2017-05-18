package de.markhaehnel.rbtv.rocketbeanstv.utils

import java.io.IOException
import java.net.InetAddress
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object NetworkHelper {
    fun hasInternet(): Boolean {
        try {
            getContentFromUrl("https://google.com")
            return true
        } catch (e: Exception) {
            return false
        }

    }

    @Throws(IOException::class)
    fun getContentFromUrl(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        return response.body().string()
    }
}
