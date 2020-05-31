package com.wittgroupinc.easyjsonrpc

sealed class RxWebSocketState {
    data class Disconnected(val b: String) : RxWebSocketState()
    data class Connected(val b: String) : RxWebSocketState()
}
