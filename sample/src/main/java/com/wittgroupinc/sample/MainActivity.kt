package com.wittgroupinc.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.wittgroupinc.easyjsonrpc.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service =
            create(MyService::class.java)

        service.myMethod(10, "Hello", emptyList())

    }

    private val logger = fun(msg: String) {
        Log.d("tag", msg)
    }
}
