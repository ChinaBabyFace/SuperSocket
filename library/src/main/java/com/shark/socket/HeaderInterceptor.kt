package com.shark.socket

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by renyuxiang on 2015/12/3.
 */
class HeaderInterceptor(private val headerMap: Map<String, String>?) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.e("shark","HeaderInterceptor intercept")
        if (headerMap == null || headerMap.isEmpty()) {
            return chain.proceed(chain.request())
        }
        Log.e("shark","HeaderInterceptor intercept2")
        val builder = chain.request().newBuilder()
        for ((key, value) in headerMap) {
            builder.addHeader(key, value)
        }

        return chain.proceed(builder.build())
    }
}
