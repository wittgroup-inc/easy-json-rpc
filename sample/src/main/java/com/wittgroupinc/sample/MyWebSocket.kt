package com.wittgroupinc.sample

import com.wittgroupinc.easyjsonrpc.RxWebSocket
import com.wittgroupinc.easyjsonrpc.RxWebSocketState
import io.reactivex.Observable
import io.reactivex.Single

class MyWebSocket : RxWebSocket<User> {
    override fun sendMessage(message: String): Single<Unit> {
        return Single.just(my())
    }

    override fun messages(): Observable<User> {
        return Observable.just(User(1, "pawan"))
    }

    override fun observeState(): Observable<RxWebSocketState> {
        return Observable.just(RxWebSocketState.Disconnected("fsdf"))
    }


    private fun my() {

    }
}
