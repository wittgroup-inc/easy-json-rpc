package com.wittgroupinc.easyjsonrpc

import android.content.Context
import com.wittgroupinc.easyjsonrpc.helpers.SslClientCertificateUtil
import com.wittgroupinc.easyjsonrpc.socket.RxWebSocketImpl

class EasyJsonRpc {
    companion object {
        lateinit var rxWebSocketImpl: RxWebSocketImpl
        fun connect(context: Context): RxWebSocketImpl {
            val certificateUtil =
                SslClientCertificateUtil()
            val socket = RxWebSocketImpl(
                certificateUtil.socketFactory(context),
                certificateUtil.sslTrustManager(context)
            )
            socket.connect()
            rxWebSocketImpl = socket
            return socket
        }
    }
}
