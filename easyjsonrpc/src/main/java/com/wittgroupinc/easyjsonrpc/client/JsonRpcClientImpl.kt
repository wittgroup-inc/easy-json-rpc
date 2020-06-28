package com.wittgroupinc.easyjsonrpc.client

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.wittgroupinc.easyjsonrpc.exceptions.JsonRpcCallException
import com.wittgroupinc.easyjsonrpc.logger.Logger
import com.wittgroupinc.easyjsonrpc.models.JsonRpcRequest
import com.wittgroupinc.easyjsonrpc.models.JsonRpcResponse
import com.wittgroupinc.easyjsonrpc.models.RxWebSocketState
import com.wittgroupinc.easyjsonrpc.serializer.Serializer
import com.wittgroupinc.easyjsonrpc.socket.RxWebSocket
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.TimeUnit

class JsonRpcClientImpl<R>(
    private val rxWebSocket: RxWebSocket,
    private val serializer: Serializer<R>,
    private val timeout: Long = 5000L,
    private val logger: Logger
) : JsonRpcClient<R> {

    override fun <R> call(request: JsonRpcRequest, responseParser: (JsonElement) -> R): Single<R> {
        val requestStr = serializer.serialize(request)
        logger.d(LOG_TAG, "JsonRpc request = $request")
        return rxWebSocket.sendMessage(requestStr)
            .flatMap {
                responses(request.id, timeout, responseParser)
            }
    }

    private fun <R> responses(
        requestId: Long,
        timeout: Long,
        responseParser: (JsonElement) -> R
    ): Single<R> {
        return rxWebSocket.messages()
            .observeOn(Schedulers.computation())
            .flatMap {
                //TODO Need to revisit
                var response: JsonRpcResponse? = null
                if (it is String) {
                    try {
                        var gson = Gson()
                        response = gson.fromJson(it, JsonRpcResponse::class.java)
                    } catch (e: Exception) {

                    }
                }
                response?.let { Observable.just(response) } ?: Observable.just(it)
            }
            .ofType(JsonRpcResponse::class.java)
            .takeUntil(
                rxWebSocket.observeState()
                    .skip(1)
                    .ofType(RxWebSocketState.Disconnected::class.java)
            )
            .switchIfEmpty(
                Observable.error(
                    JsonRpcCallException(
                        JSON_RPC_CLOSED,
                        "WS closed or failed"
                    )
                )
            )
            .filter { response ->
                response.id == requestId
            }
            .timeout(timeout, TimeUnit.MILLISECONDS, Schedulers.computation())
            .firstOrError()
            .flatMap { response ->
                when {
                    response.error != null -> {
                        val ex =
                            JsonRpcCallException(
                                response.error.code,
                                response.error.message
                            )
                        Single.error(ex)
                    }

                    else -> {
                        val result = responseParser(response.result)
                        Single.just(result)
                    }
                }
            }

    }

    companion object {
        val LOG_TAG = "JsonRpcClientImpl"
        val JSON_RPC_CLOSED = 100
    }

}
