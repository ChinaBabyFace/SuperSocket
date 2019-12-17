package com.shark.socket.demo

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shark.socket.SocketListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var superSocket: CustomerSocket
    lateinit var connectButton: Button
    lateinit var closeButton: Button
    lateinit var sendButton: Button
    lateinit var inputText: EditText
    lateinit var idText: EditText
    lateinit var tv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        superSocket = CustomerSocket(
            BuildConfig.SOCKET_URL,
            HashMap<String, String>().apply { put("token", "stopworld") }).apply { init() }

        connectButton = button
        closeButton = button2
        sendButton = button3
        inputText = editText
        idText = editText2
        tv = textView

        connectButton.setOnClickListener {
            connect()
        }
        closeButton.setOnClickListener {
            superSocket.close()
        }
        sendButton.setOnClickListener {
            if (!TextUtils.isEmpty(inputText.text.toString())) {
                tv.append(inputText.text.toString() + "\n")
                superSocket.send(
                    "{" +
                            "  \"receiveId\": \"${idText.text}\"," +
                            "  \"msg\": \"${inputText.text}\"" +
                            "}"
                )
                inputText.setText("")
            }
        }
    }

    private fun connect() {
        superSocket.connect(object : SocketListener {
            override fun onClosed(code: Int, reason: String) {
                runOnUiThread { tv.append("Closed:${reason}\n") }
            }

            override fun onClosing(code: Int, reason: String) {
                runOnUiThread { tv.append("Closing\n") }
            }

            override fun onMessage(text: String) {
                Log.e("Shark", "onMessage:${text}")
                runOnUiThread { tv.append(text + "\n") }
            }

            override fun onOpen(response: okhttp3.Response) {
                Log.e("Shark", "onOpen:${response}")
                runOnUiThread { tv.append("Connect Success:${response.code}\n") }
            }

            override fun onMessage(bytes: okio.ByteString) {
            }

            override fun onFailure(t: Throwable, response: okhttp3.Response?) {
                Log.e("Shark", "onFailure:${response}")
                runOnUiThread { tv.append("Error:${t.message}\n") }
            }
        })
    }
}
