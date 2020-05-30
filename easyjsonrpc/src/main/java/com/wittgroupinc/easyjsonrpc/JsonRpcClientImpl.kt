package com.wittgroupinc.easyjsonrpc

import com.google.gson.JsonElement
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class JsonRpcClientImpl<R>(
    private val rxWebSocket: RxWebSocket<R>,
    private val serializer: Deserializer<R>,
    private val timeout: Long = 5000L,
    private val logger: Logger
) : JsonRpcClient<R> {

    override fun <R> call(request: JsonRpcRequest, responseParser: (JsonElement) -> R): Single<R> {
        val requestStr = serializer.serialize(request)
        logger.d(LOG_TAG, "JsonRpc request = $request")
        return rxWebSocket.sendMessage(requestStr)
            .flatMap { responses(request.id, timeout, responseParser) }
    }

    private fun <T> responses(
        requestId: Long,
        timeout: Long,
        responseParser: (JsonElement) -> T
    ): Single<T> {
        return rxWebSocket.messages()
            .observeOn(Schedulers.computation())
            .ofType(JsonRpcResponse::class.java)
            .doOnNext { logger.d(LOG_TAG, "JsonRpc response = $it") }
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
            .filter { response -> response.id == requestId }
            .timeout(timeout, TimeUnit.MILLISECONDS, Schedulers.computation())
            .firstOrError()
            .flatMap { response ->
                when {
                    response.error != null -> {
                        val ex = JsonRpcCallException(response.error.code, response.error.message)
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
        val LOG_TAG = "Hello"
        val JSON_RPC_CLOSED = 100
    }

}