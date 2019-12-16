package com.shark.socket

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import java.util.concurrent.TimeUnit

class SuperSocket() {
    private lateinit var httpClient: OkHttpClient
    private lateinit var baseUrl: String
    private lateinit var coreSocket: WebSocket
    private var state: SocketState = SocketState.CLOSED

    constructor(url: String) : this() {
        baseUrl = url
        initSocket()
    }

    private fun initSocket() {
        httpClient = OkHttpClient.Builder()
            .pingInterval(3, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
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
        httpClient.dispatcher.cancelAll()
        coreSocket.close(1000, "Normal")
    }
}