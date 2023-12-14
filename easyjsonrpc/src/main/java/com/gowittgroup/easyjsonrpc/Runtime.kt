package com.gowittgroup.easyjsonrpc

import android.content.Context
import com.gowittgroup.easyjsonrpc.client.JsonRpcClient
import com.gowittgroup.easyjsonrpc.client.JsonRpcClientImpl
import com.gowittgroup.easyjsonrpc.helpers.SslClientCertificateUtil
import com.gowittgroup.easyjsonrpc.logger.Logger
import com.gowittgroup.easyjsonrpc.serializer.Serializer
import com.gowittgroup.easyjsonrpc.serializer.SerializerImpl
import com.gowittgroup.easyjsonrpc.socket.RxWebSocket
import com.gowittgroup.easyjsonrpc.socket.RxWebSocketImpl

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




