package com.shark.socket

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import java.util.concurrent.TimeUnit

open class SuperSocket() {
    private lateinit var httpClient: OkHttpClient
    private lateinit var baseUrl: String
    private lateinit var coreSocket: WebSocket
    private var state: SocketState = SocketState.START

    constructor(url: String) : this() {
        baseUrl = url
    }

    open fun getHttpClientBuilder(): OkHttpClient.Builder =
        OkHttpClient.Builder()
            .pingInterval(3, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG)
                    level = HttpLoggingInterceptor.Level.BODY
            })

    fun init() {
        if (state == SocketState.START) {
            httpClient = getHttpClientBuilder().build()
            state = SocketState.CLOSED
        }
    }

    fun connect(listener: SocketListener?) {
        if (state == SocketState.CONNECTED) return
        httpClient.newWebSocket(Request.Builder().url(baseUrl).build(),
            object : WebSocketListener() {
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    state = SocketState.CLOSED
                    listener?.onClosed(code, reason)
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosing(webSocket, code, reason)
                    state = SocketState.CLOSING
                    listener?.onClosing(code, reason)
                }

                override fun onFailure(
                    webSocket: WebSocket, t: Throwable, response: Response?
                ) {
                    super.onFailure(webSocket, t, response)
                    state = SocketState.FAILURE
                    listener?.onFailure(t, response)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    listener?.onMessage(text)
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    super.onMessage(webSocket, bytes)
                    listener?.onMessage(bytes)
                }

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    state = SocketState.CONNECTED
                    coreSocket = webSocket
                    listener?.onOpen(response)
                }
            })
    }

    fun send(message: String) {
        if (state != SocketState.CONNECTED) return
        coreSocket.send(message)
    }

    fun close() {
        state = SocketState.CLOSED
        httpClient.dispatcher.cancelAll()
        coreSocket.close(1000, "Normal")
    }
}