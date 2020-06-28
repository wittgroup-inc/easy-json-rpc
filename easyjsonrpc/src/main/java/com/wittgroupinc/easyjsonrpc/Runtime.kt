package com.wittgroupinc.easyjsonrpc

import android.content.Context
import com.wittgroupinc.easyjsonrpc.client.JsonRpcClient
import com.wittgroupinc.easyjsonrpc.client.JsonRpcClientImpl
import com.wittgroupinc.easyjsonrpc.helpers.SslClientCertificateUtil
import com.wittgroupinc.easyjsonrpc.logger.Logger
import com.wittgroupinc.easyjsonrpc.serializer.Serializer
import com.wittgroupinc.easyjsonrpc.serializer.SerializerImpl
import com.wittgroupinc.easyjsonrpc.socket.RxWebSocket
import com.wittgroupinc.easyjsonrpc.socket.RxWebSocketImpl

class Runtime {
    companion object {
        lateinit var socket: RxWebSocket

        fun <T> getClient(): JsonRpcClient<T> {
            return JsonRpcClientImpl(
                socket, getSerializer(), 300000L,
                Logger()
            )
        }

        fun init(context: Context) {
            socket = connect(context)
        }

        fun <T> getSerializer(): Serializer<T> {
            return SerializerImpl()
        }

        private fun connect(context: Context): RxWebSocket {
            val certificateUtil =
                SslClientCertificateUtil()
            val socket = RxWebSocketImpl(
                certificateUtil.socketFactory(context),
                certificateUtil.sslTrustManager(context)
            )
            socket.connect()
            return socket
        }
    }
}




