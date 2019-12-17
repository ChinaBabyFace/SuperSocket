package com.shark.socket.demo

import com.shark.socket.HeaderInterceptor
import com.shark.socket.SuperSocket
import okhttp3.OkHttpClient

class CustomerSocket(url: String, var map: Map<String, String>) : SuperSocket(url) {
    override fun getHttpClientBuilder(): OkHttpClient.Builder {
        return super.getHttpClientBuilder().apply { addInterceptor(HeaderInterceptor(map)) }
    }
}