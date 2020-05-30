package com.wittgroupinc.easyjsonrpc

import io.reactivex.Observable
import io.reactivex.Single

class MyWebSocket : RxWebSocket<Any> {
    override fun sendMessage(message: String): Single<Unit> {
        return Single.just(my())
    }

    override fun messages(): Observable<Any> {
        return Observable.just("")
    }

    override fun observeState(): Observable<RxWebSocketState> {
        return Observable.just(RxWebSocketState.Disconnected("fsdf"))
    }

    private fun my() {

    }
}
