package com.wittgroupinc.easyjsonrpc

import com.google.gson.JsonElement
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class JsonRpcClientImpl<R>(
    private val rxWebSocket: RxWebSocket<Any>,
    private val serializer: Deserializer<R>,
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
            .log("chutiya1")
            .ofType(JsonRpcResponse::class.java)
            .log("chutiya2")
            .takeUntil(
                rxWebSocket.observeState()
                    .log("chutiya3")
                    //.skip(1)
                    //.ofType(RxWebSocketState.Disconnected::class.java)
                    .log("chutiya4")
            )
            .switchIfEmpty(
                Observable.error(
                    JsonRpcCallException(
                        JSON_RPC_CLOSED,
                        "WS closed or failed"
                    )
                )
            )
            .filter {
                    response -> response.id == requestId
            }
            .log("chutiya3")
            .timeout(timeout, TimeUnit.MILLISECONDS, Schedulers.computation())
            .log("chutiya3")
            .firstOrError()
            .log("chutiya3")
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
