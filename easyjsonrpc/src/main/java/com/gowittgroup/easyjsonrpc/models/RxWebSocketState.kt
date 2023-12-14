package com.gowittgroup.easyjsonrpc.models

sealed class RxWebSocketState {
    data class Disconnected(val b: String) : RxWebSocketState()
    data class Connected(val b: String) : RxWebSocketState()
}
