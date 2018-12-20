package de.markhaehnel.rbtv.rocketbeanstv.util

import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*


/**
 * Adds a custom `User-Agent` header to OkHttp requests.
 */
class UserAgentInterceptor(val userAgent: String) : Interceptor {

    constructor(appName: String, appVersion: String) : this(
        String.format(
            Locale.US,
            "%s/%s (Android %s; %s; %s %s; %s)",
            appName,
            appVersion,
            Build.VERSION.RELEASE,
            Build.MODEL,
            Build.BRAND,
            Build.DEVICE,
            Locale.getDefault().getLanguage()
        )
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentRequest = chain.request()
            .newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(userAgentRequest)
    }
}