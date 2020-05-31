package com.wittgroupinc.sample

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.wittgroupinc.easyjsonrpc.*
import io.reactivex.android.schedulers.AndroidSchedulers

class MainActivity : AppCompatActivity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service =
            create(MyService::class.java, this)

        val result = service.myMethod(10, "Hello", emptyList())
        result
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { a ->
                Log.d("MainActivity", "success->${a.toString()}")
            }
            .doOnError {

                Log.d("MainActivity", "error->$it")
            }
            .subscribe()

    }


    private val logger = fun(msg: String) {
        Log.d("tag", msg)
    }
}
