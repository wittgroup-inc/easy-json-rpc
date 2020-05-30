package com.wittgroupinc.easyjsonrpc

import io.reactivex.Observable
import io.reactivex.Single

interface RxWebSocket<T> {

    fun sendMessage(message: String): Single<Unit>

    fun messages(): Observable<T>

    fun observeState(): Observable<RxWebSocketState>

}
