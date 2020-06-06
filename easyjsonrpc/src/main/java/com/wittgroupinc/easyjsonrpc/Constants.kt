package com.wittgroupinc.easyjsonrpc

interface Constants {
    companion object {
        const val PROTOCOL = "wss://"
        const val PORT = 8887
        const val TSL_PROTOCOL = "TLS"
        const val SERVER_ADDRESS = "192.168.0.2"
        const val SUB_PROTOCAL_HEADER_KEY = "Sec-WebSocket-Protocol"
        const val SUB_PROTOCAL_HEADER_VALUE = "com.wittgroup.easyjsonrpc.v1"
    }
}
