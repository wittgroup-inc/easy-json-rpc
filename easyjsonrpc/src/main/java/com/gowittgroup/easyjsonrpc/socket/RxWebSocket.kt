package com.gowittgroup.easyjsonrpc.socket

import com.gowittgroup.easyjsonrpc.models.RxWebSocketState
import io.reactivex.Observable
import io.reactivex.Single

interface RxWebSocket {

    fun sendMessage(message: String): Single<Unit>

    fun messages(): Observable<Any>

    fun observeState(): Observable<RxWebSocketState>

}
