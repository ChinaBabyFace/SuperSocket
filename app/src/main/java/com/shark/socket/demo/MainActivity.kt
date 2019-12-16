package com.shark.socket.demo

import android.database.Observable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import com.shark.socket.SocketListener
import com.shark.socket.SuperSocket
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDispose
import io.reactivex.Observer
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.operators.observable.ObservableTimer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var superSocket: SuperSocket
    lateinit var connectButton: Button
    lateinit var closeButton: Button
    lateinit var sendButton: Button
    lateinit var inputText: EditText
    lateinit var tv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        superSocket = SuperSocket(BuildConfig.SOCKET_URL)

        connectButton = button
        closeButton = button2
        sendButton = button3
        inputText = editText
        tv = textView

        connectButton.setOnClickListener {
            connect()
        }
        closeButton.setOnClickListener {
            superSocket.close()
        }
        sendButton.setOnClickListener {
            if (!TextUtils.isEmpty(inputText.text.toString())) {
                tv.append(inputText.text.toString()+"\n")
                superSocket.send(inputText.text.toString())
                inputText.setText("")
            }
        }
//        ObservableTimer.interval(3, TimeUnit.SECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .autoDispose(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY))
//            .subscribe(object : Observer<Long> {
//                override fun onNext(t: Long) {
////                    superSocket.send("Heart Heat:${t}")
//                }
//
//                override fun onComplete() {
//                }
//
//                override fun onSubscribe(d: Disposable) {
//                }
//
//                override fun onError(e: Throwable) {
//                }
//            })
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
                runOnUiThread { tv.append(text+"\n") }
            }

            override fun onOpen(response: okhttp3.Response) {
                Log.e("Shark", "onOpen:${response.code}")
                runOnUiThread { tv.append("Connect Success:${response.code}\n") }
            }

            override fun onMessage(bytes: okio.ByteString) {
            }

            override fun onFailure(t: Throwable, response: okhttp3.Response?) {
                Log.e("Shark", "onFailure:${t.message}")
                runOnUiThread { tv.append("Error:${t.message}\n") }
            }
        })
    }
}
