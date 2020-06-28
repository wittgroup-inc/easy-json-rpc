package com.wittgroupinc.easyjsonrpc.socket

import android.util.Log
import com.wittgroupinc.easyjsonrpc.Constants
import com.wittgroupinc.easyjsonrpc.Constants.Companion.PORT
import com.wittgroupinc.easyjsonrpc.models.RxWebSocketState
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import okhttp3.*
import okio.ByteString
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class RxWebSocketImpl(
    private val sslSocketFactory: SSLSocketFactory,
    private val sslTrustManager: X509TrustManager
) : RxWebSocket, WebSocketListener() {
    private val statePublisher : PublishSubject<RxWebSocketState> = PublishSubject.create()
    private val state = statePublisher.subscribeOn(Schedulers.io())
    private val messagePublisher: PublishSubject<Any> = PublishSubject.create()
    private val message = messagePublisher.subscribeOn(Schedulers.io())
    private val dummyVerifier = HostnameVerifier { hostname, session -> true }
    private var webSocket: WebSocket? = null

    override fun sendMessage(message: String): Single<Unit> {
        return Single.just(send(message))
    }

    override fun messages(): Observable<Any> {
        return message
    }

    override fun observeState(): Observable<RxWebSocketState> {
        return state
    }

    private fun send(msg: String) {
        webSocket?.send(msg)
    }

    fun connect() {
        sslConnectionRequest(Constants.SERVER_ADDRESS, PORT)
    }

    private fun sslConnectionRequest(address: String, port: Int) {
        val client = OkHttpClient.Builder()
            .sslSocketFactory(
                sslSocketFactory,
                sslTrustManager
            )
            .hostnameVerifier(dummyVerifier)
            .build()

        val url = Constants.PROTOCOL + "$address:$port"
        val request = Request.Builder()
            .url(url)
//            .addHeader(SUB_PROTOCOL_HEADER_KEY, SUB_PROTOCOL_HEADER_VALUE)
            .build()
        client.newWebSocket(request, this)
    }


    override fun onOpen(webSocket: WebSocket, response: Response) {
        this.webSocket = webSocket
        Log.i(TAG, "Connection accepted!")
        statePublisher.onNext(
            RxWebSocketState.Connected(
                "Connected"
            )
        )
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        messagePublisher.onNext(bytes.toByteArray())
        Log.i(TAG, "onMessage ::: ${bytes.toByteArray()}")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        messagePublisher.onNext(text)
        Log.i(TAG, "onMessage(String) ::: ${text}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.i(TAG, "Closing : $code / $reason")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.i(TAG, "onClosed : $reason")
        statePublisher.onNext(
            RxWebSocketState.Disconnected(
                "Connected"
            )
        )
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.i(TAG, "Error : " + t.printStackTrace())
    }

    companion object {
        private val TAG = RxWebSocketImpl::class.java.simpleName
    }
}
