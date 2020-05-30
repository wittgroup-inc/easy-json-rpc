package com.wittgroupinc.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.wittgroupinc.easyjsonrpc.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val socket = MyWebSocket()
        val deserializer = MyDeserializer<User>()
        val jsonRpcClient: JsonRpcClientImpl<User> =
            JsonRpcClientImpl(socket, deserializer, 100L, Logger())
        val service =
            createJsonRpcService(MyService::class.java, jsonRpcClient, deserializer, logger)
        service.myMethod(10, "Hello", emptyList())

        socket.messages()

    }

    private val logger = fun(msg: String) {
        Log.d("tag", msg)
    }
}
