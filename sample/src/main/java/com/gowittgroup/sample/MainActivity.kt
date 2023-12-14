package com.gowittgroup.sample

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gowittgroup.easyjsonrpc.*
import com.gowittgroup.sample.databinding.ActivityMainBinding
import io.reactivex.android.schedulers.AndroidSchedulers


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Runtime.init(this)
        val client = EasyJsonRpc()
        val service =
            client.create(MyService::class.java)
        binding.sendRequest.setOnClickListener { v ->
            run {
                val result = service.myMethod(10, "Hello", emptyList())
                result
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess { a ->
                        Log.d("MainActivity", "success->${a}")
                    }
                    .doOnError {

                        Log.d("MainActivity", "error->$it")
                    }
                    .subscribe()
            }
        }


    }


    private val logger = fun(msg: String) {
        Log.d("tag", msg)
    }
}
