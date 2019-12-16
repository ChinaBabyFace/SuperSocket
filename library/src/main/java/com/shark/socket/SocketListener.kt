package com.shark.socket

import android.os.Parcel
import android.os.Parcelable
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

interface  SocketListener {
     fun onClosed(code: Int, reason: String)
     fun onClosing(code: Int, reason: String)
     fun onFailure(t: Throwable, response: Response?)
     fun onMessage(text: String)
     fun onMessage(bytes: ByteString)
     fun onOpen(response: Response)
}