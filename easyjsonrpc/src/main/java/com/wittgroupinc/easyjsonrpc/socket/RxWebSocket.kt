package com.wittgroupinc.easyjsonrpc.socket

import com.wittgroupinc.easyjsonrpc.models.RxWebSocketState
import io.reactivex.Observable
import io.reactivex.Single

interface RxWebSocket<T> {

    fun sendMessage(message: String): Single<Unit>

    fun messages(): Observable<T>

    fun observeState(): Observable<RxWebSocketState>

}
